package io.github.hoooosi.imagehosting.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.imagehosting.annotation.AuthLogged;
import io.github.hoooosi.imagehosting.annotation.AuthPermission;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.Permission;
import io.github.hoooosi.imagehosting.dto.BaseRes;
import io.github.hoooosi.imagehosting.dto.AddSpaceReq;
import io.github.hoooosi.imagehosting.dto.EditSpaceReq;
import io.github.hoooosi.imagehosting.dto.QuerySpaceReq;
import io.github.hoooosi.imagehosting.service.SpaceService;
import io.github.hoooosi.imagehosting.vo.SpaceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/space")
@Slf4j
@AllArgsConstructor
@Tag(name = "Space I/F")
public class SpaceController {
    private final SpaceService spaceService;

    @GetMapping("/{spaceId}")
    @AuthPermission(mask = Permission.SPACE_VIEW, id = ID.spaceId)
    @Operation(summary = "QUERY SPACE")
    public BaseRes<SpaceVO> get(@PathVariable Long spaceId) {
        return BaseRes.success(spaceService.get(spaceId));
    }

    @GetMapping("/page")
    @Operation(summary = "QUERY SPACES BY PUBLIC")
    public BaseRes<Page<SpaceVO>> pageByPublic(QuerySpaceReq req,
                                               @RequestParam(required = false) boolean notShowJoined) {
        return BaseRes.success(spaceService.page(req, Wrappers
                .lambdaQuery(SpaceVO.class)
                .apply(notShowJoined, "permission_mask != 0")
                .apply("(public_permission_mask & {0}) = {1}", Permission.SPACE_SEARCH, Permission.SPACE_SEARCH)));
    }

    @GetMapping("/page/u")
    @Operation(summary = "QUERY SPACES BY OPERATOR")
    @AuthLogged
    public BaseRes<Page<SpaceVO>> pageByOperator(QuerySpaceReq req,
                                                 @RequestParam(required = false) boolean isOwner) {
        return BaseRes.success(spaceService.page(req, Wrappers
                .lambdaQuery(SpaceVO.class)
                .apply("permission_mask != 0")
                .like(isOwner, SpaceVO::getPermissionMask, Long.MAX_VALUE)));
    }

    @PostMapping
    @AuthLogged
    @Operation(summary = "ADD SPACE")
    public BaseRes<Void> create(@RequestBody AddSpaceReq req) {
        spaceService.create(req);
        return BaseRes.success();
    }

    @PutMapping
    @Operation(summary = "EDIT SPACE")
    @AuthPermission(mask = Permission.SPACE_MANGE, id = ID.spaceId)
    public BaseRes<Void> edit(@RequestBody EditSpaceReq req) {
        spaceService.edit(req);
        return BaseRes.success();
    }

    @DeleteMapping("/{spaceId}")
    @Operation(summary = "DELETE SPACE")
    @AuthPermission(mask = Permission.SPACE_MANGE, id = ID.spaceId)
    public BaseRes<Void> delete(@PathVariable Long spaceId) {
        spaceService.delete(spaceId);
        return BaseRes.success();
    }
}