package io.github.hoooosi.imagehosting.mapper;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import io.github.hoooosi.imagehosting.vo.ImageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ImageIndexMapper extends ImageIndexBaseMapper {

    default Page<ImageVO> query(Page<ImageVO> page,
                                @Param(Constants.WRAPPER) LambdaQueryWrapper<ImageVO> wrapper) {
        return query(page, wrapper, false);
    }

    Page<ImageVO> query(Page<ImageVO> page,
                        @Param(Constants.WRAPPER) LambdaQueryWrapper<ImageVO> wrapper,
                        @Param("mask") boolean mask);

    List<Long> getAllowImgIds(List<Long> ids, Long userId);

    ImageFile getFirstFileByIdxId(@Param("idxId") Long idxId);

    long deleteAndSumSize(List<Long> ids);
}
