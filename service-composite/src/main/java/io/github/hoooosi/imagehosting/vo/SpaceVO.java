package io.github.hoooosi.imagehosting.vo;

import io.github.hoooosi.imagehosting.entity.Space;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class SpaceVO extends Space {
    private Long permissionMask;
}
