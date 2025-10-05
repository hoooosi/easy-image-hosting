package io.github.hoooosi.imagehosting.controller;

import io.github.hoooosi.imagehosting.annotation.AuthLogged;
import io.github.hoooosi.imagehosting.annotation.AuthPermission;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.Permission;
import io.github.hoooosi.imagehosting.dto.BaseRes;
import io.github.hoooosi.imagehosting.dto.EditMemberReq;
import io.github.hoooosi.imagehosting.service.MemberService;
import io.github.hoooosi.imagehosting.vo.MemberVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@Slf4j
@AllArgsConstructor
@Tag(name = "Member I/F")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/list")
    @Operation(summary = "QUERY MEMBER")
    @AuthPermission(mask = Permission.SPACE_VIEW, id = ID.spaceId)
    public BaseRes<List<MemberVO>> list(Long spaceId) {
        return BaseRes.success(memberService.list(spaceId));
    }

    @PutMapping
    @Operation(summary = "EDIT MEMBER")
    @AuthPermission(mask = Permission.SPACE_MANGE, id = ID.memberId)
    public BaseRes<Void> edit(@RequestBody EditMemberReq req) {
        memberService.edit(req);
        return BaseRes.success();
    }

    @PostMapping("/quit/{spaceId}")
    @Operation(summary = "EXIT SPACE")
    @AuthLogged
    public BaseRes<Void> quit(@PathVariable Long spaceId) {
        memberService.exit(spaceId);
        return BaseRes.success();
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "REMOVE MEMBER")
    @AuthPermission(mask = Permission.SPACE_MANGE, id = ID.memberId)
    public BaseRes<Void> remove(@PathVariable @NotNull Long memberId) {
        memberService.remove(memberId);
        return BaseRes.success();
    }
}
