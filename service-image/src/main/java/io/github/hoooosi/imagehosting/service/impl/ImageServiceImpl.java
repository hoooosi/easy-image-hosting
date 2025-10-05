package io.github.hoooosi.imagehosting.service.impl;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.imagehosting.constant.CacheNames;
import io.github.hoooosi.imagehosting.dto.PageReq;
import io.github.hoooosi.imagehosting.dto.QueryImageVOParams;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import io.github.hoooosi.imagehosting.entity.ImageIndex;
import io.github.hoooosi.imagehosting.entity.ImageItem;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
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
import io.github.hoooosi.imagehosting.vo.ImageVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ImageServiceImpl extends ServiceImpl<ImageIndexMapper, ImageIndex> implements ImageService {
    private final MinioManager minioManager;
    private final ImageIndexMapper imageIndexMapper;
    private final ImageManager uploadManager;
    private final IdentifierGenerator identifierGenerator;
    private final ImageItemBaseMapper imageItemBaseMapper;
    private final ImageFileBaseMapper imageFileBaseMapper;
    private final ImageManager imageManager;
    private final SpaceBaseMapper spaceBaseMapper;

    @Override
    public Page<ImageVO> pagePublic(PageReq req, QueryImageVOParams params) {
        return baseMapper.queryPublic(PageUtils.of(req), params);
    }

    @Override
    public Page<ImageVO> pageAll(PageReq req, QueryImageVOParams params) {
        return baseMapper.queryAll(PageUtils.of(req), params);
    }

    @Override
    @Transactional
    public void upload(MultipartFile multipartFile, Long spaceId) {

        String contentType = multipartFile.getContentType();
        ImageUtils.getFormatName(contentType);
        Long userId = SessionUtils.getUserId();

        // Add image index and image item into database
        Long idxId = (Long) identifierGenerator.nextId(new ImageIndex());
        Long itemId = (Long) identifierGenerator.nextId(new ImageItem());

        ImageIndex imageIndex = new ImageIndex()
                .setSpaceId(spaceId)
                .setUserId(userId)
                .setFirstItemId(idxId)
                .setName(multipartFile.getOriginalFilename());
        imageIndex.setId(idxId);

        ImageItem imageItem = new ImageItem()
                .setSpaceId(spaceId)
                .setIdxId(idxId)
                .setContentType(contentType)
                .setStatus(ImageItem.Status.PROCESSING);
        imageItem.setId(itemId);
        imageIndex.setFirstItemId(imageItem.getId());

        // Insert image entity into database
        ThrowUtils.throwIfZero(baseMapper.insert(imageIndex), ErrorCode.DATA_SAVE_ERROR);
        ThrowUtils.throwIfZero(imageItemBaseMapper.insert(imageItem), ErrorCode.DATA_SAVE_ERROR);

        // Process image upload
        uploadManager.uploadProcess(multipartFile, imageItem);
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