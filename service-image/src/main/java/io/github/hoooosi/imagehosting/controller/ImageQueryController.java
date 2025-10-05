package io.github.hoooosi.imagehosting.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.imagehosting.annotation.AuthLogged;
import io.github.hoooosi.imagehosting.annotation.AuthPermission;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.Permission;
import io.github.hoooosi.imagehosting.dto.BaseRes;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.dto.QueryImgReq;
import io.github.hoooosi.imagehosting.service.ImageService;
import io.github.hoooosi.imagehosting.vo.ImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image")
@Slf4j
@AllArgsConstructor
@Tag(name = "Image Query I/F")
public class ImageQueryController {
    private final ImageService imageService;

    @GetMapping("/page")
    @Operation(summary = "QUERY IMAGES BY PUBLIC")
    public BaseRes<Page<ImageVO>> pageByPublic(QueryImgReq req,
                                               @RequestParam(required = false) Long uid,
                                               @RequestParam(required = false) Long sid) {
        return BaseRes.success(imageService.query(req, Wrappers
                .lambdaQuery(ImageVO.class)
                .apply("(public_permission_mask & {0}) = {1}", Permission.SPACE_VIEW, Permission.SPACE_VIEW)
                .eq(uid != null, ImageVO::getUserId, uid)
                .eq(sid != null, ImageVO::getSpaceId, sid), true));
    }

    @GetMapping("/page/s/{spaceId}")
    @AuthPermission(mask = Permission.IMAGE_VIEW, id = ID.spaceId)
    @Operation(summary = "QUERY IMAGES BY SPACE")
    public BaseRes<Page<ImageVO>> pageBySpace(QueryImgReq req,
                                              @PathVariable @NotNull Long spaceId,
                                              @RequestParam(required = false) Long uid) {
        return BaseRes.success(imageService.query(req, Wrappers
                .lambdaQuery(ImageVO.class)
                .eq(uid != null, ImageVO::getUserId, uid)
                .eq(ImageVO::getSpaceId, spaceId), false));
    }

    @GetMapping("/page/u")
    @AuthLogged
    @Operation(summary = "QUERY IMAGES BY OPERATOR")
    public BaseRes<Page<ImageVO>> pageByOperator(QueryImgReq req,
                                                 @RequestParam(required = false) Long spaceId) {
        return BaseRes.success(imageService.query(req, Wrappers
                .lambdaQuery(ImageVO.class)
                .eq(spaceId != null, ImageVO::getSpaceId, spaceId)
                .eq(ImageVO::getUserId, SessionUtils.getUserId()), false));
    }

}
