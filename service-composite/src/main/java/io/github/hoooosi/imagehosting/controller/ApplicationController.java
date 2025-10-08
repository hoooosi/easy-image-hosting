package io.github.hoooosi.imagehosting.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.imagehosting.annotation.AuthLogged;
import io.github.hoooosi.imagehosting.annotation.AuthPermission;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.Permission;
import io.github.hoooosi.imagehosting.dto.BaseRes;
import io.github.hoooosi.imagehosting.dto.PageReq;
import io.github.hoooosi.imagehosting.entity.Application;
import io.github.hoooosi.imagehosting.dto.ApplicationHandleReq;
import io.github.hoooosi.imagehosting.service.ApplicationService;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.vo.ApplicationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@Slf4j
@AllArgsConstructor
@Tag(name = "Application I/F")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/list/{spaceId}")
    @Operation(summary = "QUERY APPLICATIONS BY SPACE")
    @AuthPermission(mask = Permission.SPACE_MANGE, id = ID.spaceId)
    public BaseRes<List<ApplicationVO>> pageBySpace(@RequestParam(required = false) Application.Status status,
                                                    @PathVariable Long spaceId) {
        return BaseRes.success(applicationService.list(Wrappers.lambdaQuery(ApplicationVO.class)
                .eq(status != null, ApplicationVO::getStatus, status)
                .eq(ApplicationVO::getSpaceId, spaceId)));
    }

    @GetMapping("/list")
    @Operation(summary = "QUERY APPLICATIONS BY OPERATOR")
    @AuthLogged
    public BaseRes<List<ApplicationVO>> pageByOperator(@RequestParam(required = false) Application.Status status) {
        return BaseRes.success(applicationService.list(Wrappers
                .lambdaQuery(ApplicationVO.class)
                .eq(ApplicationVO::getUserId, SessionUtils.getUserId())
                .eq(status != null, ApplicationVO::getStatus, status)));
    }

    @PostMapping("/apply/{spaceId}")
    @Operation(summary = "APPLY TO JOIN SPACE")
    @AuthPermission(mask = Permission.SPACE_JOIN, id = ID.spaceId)
    public BaseRes<Void> apply(@NotNull @PathVariable Long spaceId) {
        applicationService.apply(spaceId);
        return BaseRes.success();
    }

    @PostMapping("/invite/{spaceId}/{uid}")
    @Operation(summary = "INVITE TO JOIN SPACE")
    @AuthPermission(mask = Permission.SPACE_MANGE, id = ID.spaceId)
    public BaseRes<Void> invite(@NotNull @PathVariable Long spaceId, @NotNull @PathVariable Long uid) {
        applicationService.invite(spaceId, uid);
        return BaseRes.success();
    }

    @PutMapping("/handle")
    @Operation(summary = "HANDLE APPLICATION")
    @AuthPermission(mask = Permission.SPACE_MANGE, id = ID.applicationId)
    public BaseRes<Void> handle(@RequestBody ApplicationHandleReq req) {
        applicationService.handle(req.getApplicationId(), req.getStatus(), Application.HandleType.MANUAL);
        return BaseRes.success();
    }
}
