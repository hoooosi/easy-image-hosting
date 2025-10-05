package io.github.hoooosi.imagehosting.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@TableName(value = "tb_image_file")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class ImageFile extends BaseEntity {
    private String md5;
    private String contentType;
    private Long size;
    private Integer width;
    private Integer height;
}
