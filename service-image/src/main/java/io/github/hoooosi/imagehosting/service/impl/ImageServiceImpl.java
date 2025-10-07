package io.github.hoooosi.imagehosting.service.impl;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.imagehosting.utils.ImageItemUtils;
import io.github.hoooosi.imagehosting.constant.CacheNames;
import io.github.hoooosi.imagehosting.dto.CheckUploadInitReq;
import io.github.hoooosi.imagehosting.dto.PageReq;
import io.github.hoooosi.imagehosting.dto.QueryImageVOParams;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import io.github.hoooosi.imagehosting.entity.ImageIndex;
import io.github.hoooosi.imagehosting.entity.ImageItem;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.manager.SpaceManager;
import io.github.hoooosi.imagehosting.mapper.ImageFileBaseMapper;
import io.github.hoooosi.imagehosting.mapper.ImageItemBaseMapper;
import io.github.hoooosi.imagehosting.mapper.SpaceBaseMapper;
import io.github.hoooosi.imagehosting.utils.ImageUtils;
import io.github.hoooosi.imagehosting.utils.PageUtils;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import io.github.hoooosi.imagehosting.manager.MinioManager;
import io.github.hoooosi.imagehosting.manager.ImageManager;
import io.github.hoooosi.imagehosting.mapper.ImageIndexMapper;
import io.github.hoooosi.imagehosting.service.ImageService;
import io.github.hoooosi.imagehosting.vo.CheckUploadInitVO;
import io.github.hoooosi.imagehosting.vo.ImageVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ImageServiceImpl extends ServiceImpl<ImageIndexMapper, ImageIndex> implements ImageService {
    private static final int MAX_BYTES = 10 * 1024 * 1024;
    private static final int EXPIRE_MINUTES = 10;

    private final MinioManager minioManager;
    private final ImageIndexMapper imageIndexMapper;
    private final IdentifierGenerator identifierGenerator;
    private final ImageItemBaseMapper imageItemBaseMapper;
    private final ImageFileBaseMapper imageFileBaseMapper;
    private final ImageManager imageManager;
    private final SpaceBaseMapper spaceBaseMapper;
    private final SpaceManager spaceManager;
    private final ImageItemUtils imageItemUtils;

    @Override
    public Page<ImageVO> pagePublic(PageReq req, QueryImageVOParams params) {
        return baseMapper.queryPublic(PageUtils.of(req), params);
    }

    @Override
    public Page<ImageVO> pageAll(PageReq req, QueryImageVOParams params) {
        return baseMapper.queryAll(PageUtils.of(req), params);
    }

    @Transactional
    @Override
    public CheckUploadInitVO upload(CheckUploadInitReq req, Long spaceId) {
        ImageUtils.getFormatName(req.getContentType());
        ThrowUtils.throwIf(req.getSize() <= 0 || req.getSize() > MAX_BYTES,
                ErrorCode.UNSUPPORTED_MEDIA_TYPE, "File size must be between 1 byte and 10MB");

        Long userId = SessionUtils.getUserId();
        String md5 = req.getMd5();

        ImageFile imageFile = imageFileBaseMapper.selectOne(Wrappers
                .lambdaQuery(ImageFile.class)
                .eq(ImageFile::getMd5, md5));

        CheckUploadInitVO result = new CheckUploadInitVO();

        ImageIndex imageIndex = new ImageIndex();
        ImageItem imageItem = new ImageItem();
        Long idxId = (Long) identifierGenerator.nextId(imageIndex);
        Long itemId = (Long) identifierGenerator.nextId(imageItem);
        imageIndex.setId(idxId);
        imageItem.setId(itemId);

        imageIndex.setSpaceId(spaceId)
                .setUserId(userId)
                .setName(req.getFilename())
                .setFirstItemId(itemId);
        imageItem
                .setIdxId(imageIndex.getId())
                .setSpaceId(spaceId)
                .setMd5(req.getMd5());

        if (imageFile == null) {
            var form = minioManager.createPresignedPost(md5, req.getContentType(), req.getSize(), EXPIRE_MINUTES);
            result.setUploaded(false)
                    .setUrl(form.url())
                    .setFormData(form.formData())
                    .setExpireAt(form.expireAt());
        } else {
            imageItemUtils.union(imageItem, imageFile);
            ThrowUtils.throwIfZero(spaceManager.adjustingUsedCapacity(imageItem.getSpaceId(), imageItem.getSize()), ErrorCode.INSUFFICIENT_CAPACITY);
            result.setUploaded(true);
        }

        ThrowUtils.throwIfZero(imageIndexMapper.insert(imageIndex), ErrorCode.DATA_SAVE_ERROR);
        ThrowUtils.throwIfZero(imageItemBaseMapper.insert(imageItem), ErrorCode.DATA_SAVE_ERROR);

        return result;
    }

    @Override
    @Transactional
    public void convert(Long idxId, String contentType) {
        ImageUtils.getFormatName(contentType);

        // Check file exists
        ImageFile imageFile = imageIndexMapper.getFirstFileByIdxId(idxId);
        ThrowUtils.throwIfNull(imageFile, ErrorCode.NOT_FOUND);

        List<ImageItem> imageItems = imageItemBaseMapper.selectList(Wrappers
                .lambdaQuery(ImageItem.class)
                .select(ImageItem::getId, ImageItem::getSpaceId)
                .eq(ImageItem::getIdxId, idxId));

        List<String> list = imageItems.stream().map(ImageItem::getContentType).toList();
        ThrowUtils.throwIf(list.contains(contentType), ErrorCode.FORMAT_ALREADY_EXISTS);

        Long spaceId = imageItems.get(0).getSpaceId();
        Long itemId = (Long) identifierGenerator.nextId(new ImageItem());
        ImageItem imageItem = new ImageItem()
                .setSpaceId(spaceId)
                .setIdxId(idxId)
                .setContentType(contentType)
                .setStatus(ImageItem.Status.PROCESSING);
        imageItem.setId(itemId);
        ThrowUtils.throwIfZero(imageItemBaseMapper.insert(imageItem), ErrorCode.DATA_SAVE_ERROR);

        imageManager.convertProcess(imageItem, imageFile.getMd5());
    }


    @Override
    @Transactional
    public void delete(Long idxId) {
        ImageIndex imageIndex = imageIndexMapper.selectById(idxId);
        long sumSize = imageIndexMapper.deleteAndSumSize(List.of(idxId));
        int flag = spaceBaseMapper.update(Wrappers.lambdaUpdate(Space.class)
                .setSql("total_size = total_size - {0}", sumSize)
                .eq(Space::getId, imageIndex.getSpaceId()));
        ThrowUtils.throwIfZero(flag, ErrorCode.DATA_SAVE_ERROR);
    }

    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        Long userId = SessionUtils.getUserIdOrThrow();
        List<Long> allowImgIds = imageIndexMapper.getAllowImgIds(ids, userId);

        List<ImageIndex> imageIndexList = imageIndexMapper.selectList(Wrappers.lambdaQuery(ImageIndex.class)
                .select(ImageIndex::getId, ImageIndex::getSpaceId)
                .in(ImageIndex::getId, allowImgIds));

        Map<Long, List<Long>> spaceIdToIndexIdsMap = imageIndexList.stream()
                .collect(Collectors.groupingBy(
                        ImageIndex::getSpaceId,
                        Collectors.mapping(
                                ImageIndex::getId,
                                Collectors.toList()
                        )));


        spaceIdToIndexIdsMap.forEach((spaceId, indexIds) -> {
            long sumSize = imageIndexMapper.deleteAndSumSize(indexIds);
            if (sumSize != 0) {
                int flag = spaceBaseMapper.update(Wrappers.lambdaUpdate(Space.class)
                        .setSql("total_size = total_size - {0}", sumSize)
                        .eq(Space::getId, spaceId));
                ThrowUtils.throwIfZero(flag, ErrorCode.DATA_SAVE_ERROR);
            }
        });
    }

    @Override
    @Cacheable(cacheNames = CacheNames.IMG_ITEM_URL, key = "#id+':'+#isThumbnail")
    public String generateTemporaryLink(Long id, boolean isThumbnail) {
        ImageItem imageItem = imageItemBaseMapper.selectById(id);
        ThrowUtils.throwIfNull(imageItem, ErrorCode.NOT_FOUND);

        ImageFile imageFile = imageFileBaseMapper.selectById(imageItem.getFileId());
        ThrowUtils.throwIfNull(imageFile, ErrorCode.NOT_FOUND);

        String objectName = isThumbnail ? "thumbnail/" + imageFile.getMd5() : imageFile.getMd5();
        return minioManager.generateTemporaryLink(objectName);
    }
}