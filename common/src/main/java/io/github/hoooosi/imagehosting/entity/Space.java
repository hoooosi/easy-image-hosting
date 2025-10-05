package io.github.hoooosi.imagehosting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@TableName(value = "tb_space")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class Space  extends BaseEntity  {
    private String name;
    private Long maxSize;
    private Long totalSize;
    private Long publicPermissionMask;
    private Long memberPermissionMask;
}