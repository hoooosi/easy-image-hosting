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
import io.github.hoooosi.imagehosting.entity.Member;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.mapper.MemberMapper;
import io.github.hoooosi.imagehosting.mapper.SpaceMapper;
import io.github.hoooosi.imagehosting.service.SpaceService;
import io.github.hoooosi.imagehosting.utils.PageUtils;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/space")
@Slf4j
@AllArgsConstructor
@Tag(name = "Space I/F")
public class SpaceController {
    private final SpaceService spaceService;
    private final MemberMapper memberMapper;
    private final SpaceMapper spaceMapper;

    @GetMapping("/{spaceId}")
    @AuthPermission(mask = Permission.SPACE_VIEW, id = ID.spaceId)
    @Operation(summary = "QUERY SPACE")
    public BaseRes<Space> get(@PathVariable Long spaceId) {
        return BaseRes.success(spaceService.lambdaQuery()
                .eq(Space::getId, spaceId)
                .one());
    }

    @GetMapping("/permission/{spaceId}")
    @Operation(summary = "QUERY PERMISSION OF SPACE")
    @AuthLogged
    public BaseRes<Long> getPermissionMask(@PathVariable Long spaceId) {
        Long mask = 0L;
        Long userId = SessionUtils.getUserId();

        Member member = memberMapper.selectOne(Wrappers.lambdaQuery(Member.class)
                .select(Member::getPermissionMask)
                .eq(Member::getSpaceId, spaceId)
                .eq(Member::getUserId, userId));

        if (member != null)
            mask = member.getPermissionMask();

        return BaseRes.success(mask);
    }

    @GetMapping("/permission")
    @Operation(summary = "QUERY PERMISSION OF SPACES")
    @AuthLogged
    public BaseRes<Map<Long, Long>> getPermissionMasks(@RequestParam List<Long> ids) {
        ids.add(0L);
        Map<Long, Long> map = memberMapper.selectList(Wrappers.lambdaQuery(Member.class)
                        .select(Member::getSpaceId, Member::getPermissionMask)
                        .eq(Member::getUserId, SessionUtils.getUserId())
                        .in(Member::getSpaceId, ids))
                .stream()
                .collect(Collectors.toMap(Member::getSpaceId, Member::getPermissionMask));
        return BaseRes.success(map);
    }

    @GetMapping("/page")
    @Operation(summary = "QUERY SPACES BY PUBLIC")
    public BaseRes<Page<Space>> pageByPublic(QuerySpaceReq req) {
        return BaseRes.success(spaceService.lambdaQuery()
                .like(req.getName() != null, Space::getName, req.getName())
                .apply(req.getMask() != null, "(public_permission_mask & {0}) = {1}", req.getMask(), req.getMask())
                .apply("(public_permission_mask & {0}) = {1}", Permission.SPACE_SEARCH, Permission.SPACE_SEARCH)
                .orderBy(true, req.isAsc(), Space::getId)
                .page(PageUtils.of(req)));
    }

    @GetMapping("/page/u")
    @Operation(summary = "QUERY SPACES BY OPERATOR")
    @AuthLogged
    public BaseRes<Page<Space>> pageByOperator(QuerySpaceReq req) {
        List<Long> spaceIds = new java.util.ArrayList<>(memberMapper.selectList(Wrappers.lambdaQuery(Member.class)
                        .select(Member::getSpaceId)
                        .eq(Member::getUserId, SessionUtils.getUserId()))
                .stream().map(Member::getSpaceId).toList());
        spaceIds.add(0L);

        return BaseRes.success(spaceService.lambdaQuery()
                .like(req.getName() != null, Space::getName, req.getName())
                .apply(req.getMask() != null, "(public_permission_mask & {0}) = {1}", req.getMask(), req.getMask())
                .orderBy(true, req.isAsc(), Space::getId)
                .in(Space::getId, spaceIds)
                .page(PageUtils.of(req)));
    }

    @GetMapping("/list/u")
    @Operation(summary = "QUERY SPACEIDS BY OPERATOR")
    @AuthLogged
    public BaseRes<List<Long>> listJoinedSpaceIds() {
        return BaseRes.success(memberMapper.selectList(Wrappers.lambdaQuery(Member.class)
                        .select(Member::getSpaceId)
                        .eq(Member::getUserId, SessionUtils.getUserId()))
                .stream().map(Member::getSpaceId).toList());
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