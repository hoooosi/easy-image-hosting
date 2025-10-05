package io.github.hoooosi.imagehosting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@TableName(value = "tb_image_index")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class ImageIndex extends BaseEntity {
    private Long firstItemId;
    private Long spaceId;
    private Long userId;
    private String name;
    private String introduction;
    private List<String> tags;
}