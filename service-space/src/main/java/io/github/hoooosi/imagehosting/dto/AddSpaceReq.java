package io.github.hoooosi.imagehosting.dto;

import io.github.hoooosi.imagehosting.constant.Permission;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
public class AddSpaceReq implements Serializable {
    @NotNull
    private String name;
    @NotNull
    private Long maxSize;
    @NotNull
    private Long publicPermissionMask;
    @NotNull
    private Long memberPermissionMask;

    public void setPublicPermissionMask(Long mask) {
        this.publicPermissionMask = mask & Permission.PUBLIC_MASK_SCOPE;
    }

    public void setMemberPermissionMask(Long mask) {
        this.memberPermissionMask = mask & Permission.MEMBER_MASK_SCOPE | Permission.PUBLIC_MASK_SCOPE;
    }
}