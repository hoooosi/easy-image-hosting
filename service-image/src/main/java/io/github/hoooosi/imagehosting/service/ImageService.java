package io.github.hoooosi.imagehosting.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.hoooosi.imagehosting.entity.ImageIndex;
import io.github.hoooosi.imagehosting.dto.QueryImgReq;
import io.github.hoooosi.imagehosting.vo.ImageVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Image service interface
 */
public interface ImageService extends IService<ImageIndex> {


    /**
     * Query image vo page
     */
    Page<ImageVO> query(QueryImgReq req, LambdaQueryWrapper<ImageVO> wrapper, boolean mask);

    /**
     * Upload image
     */
    void upload(MultipartFile file, Long spaceId);

    /**
     * Convert image format
     */
    void convert(Long idxId, String contentType);

    /**
     * Delete images
     */
    void delete(Long idxId);

    /**
     * Delete images batch
     */
    void deleteBatch(List<Long> ids);

    /**
     * Generate temporary link
     */
    String generateTemporaryLink(Long id, boolean isThumbnail);
}