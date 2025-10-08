package io.github.hoooosi.imagehosting.mapper;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.imagehosting.dto.QueryImageVOParams;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import io.github.hoooosi.imagehosting.vo.ImageVO;
import java.util.List;


public interface ImageIndexMapper extends ImageIndexBaseMapper {


    Page<ImageVO> queryPublic(Page<ImageVO> page, QueryImageVOParams params);

    Page<ImageVO> queryAll(Page<ImageVO> page, QueryImageVOParams params);

    List<Long> getAllowImgIds(List<Long> ids, Long userId);

    ImageFile getFirstFileByIdxId(Long idxId);

    long deleteAndSumSize(List<Long> ids);

    List<String> getAllTags();
}
