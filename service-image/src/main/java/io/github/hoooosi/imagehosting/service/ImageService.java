package io.github.hoooosi.imagehosting.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.hoooosi.imagehosting.dto.CheckUploadInitReq;
import io.github.hoooosi.imagehosting.dto.PageReq;
import io.github.hoooosi.imagehosting.dto.QueryImageVOParams;
import io.github.hoooosi.imagehosting.entity.ImageIndex;
import io.github.hoooosi.imagehosting.vo.CheckUploadInitVO;
import io.github.hoooosi.imagehosting.vo.ImageVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Image service interface
 */
public interface ImageService extends IService<ImageIndex> {


    Page<ImageVO> pagePublic(PageReq req, QueryImageVOParams params);

    Page<ImageVO> pageAll(PageReq req, QueryImageVOParams params);

    CheckUploadInitVO upload(CheckUploadInitReq req, Long spaceId);

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

    /**
     * Get all distinct tags from images
     */
    List<String> getAllTags();
}