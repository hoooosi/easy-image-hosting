package io.github.hoooosi.imagehosting.dto;

import io.github.hoooosi.imagehosting.constant.Permission;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditMemberReq {
    @NotNull
    private Long memberId;
    @NotNull
    private Long mask;

    public void setMask(Long mask) {
        this.mask = mask & Permission.MEMBER_MASK_SCOPE | Permission.MEMBER_MASK_SCOPE;
    }
}
