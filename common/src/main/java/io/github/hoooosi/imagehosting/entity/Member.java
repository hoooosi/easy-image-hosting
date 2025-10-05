package io.github.hoooosi.imagehosting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@TableName(value = "tb_member")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class Member extends BaseEntity {
    private Long spaceId;
    private Long userId;
    private Long permissionMask;
}