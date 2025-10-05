package io.github.hoooosi.imagehosting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageFileBaseMapper extends BaseMapper<ImageFile> {
}
