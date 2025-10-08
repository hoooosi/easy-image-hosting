package io.github.hoooosi.imagehosting.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.imagehosting.annotation.AuthLogged;
import io.github.hoooosi.imagehosting.annotation.AuthPermission;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.Permission;
import io.github.hoooosi.imagehosting.dto.BaseRes;
import io.github.hoooosi.imagehosting.dto.PageImgReq;
import io.github.hoooosi.imagehosting.service.ImageService;
import io.github.hoooosi.imagehosting.vo.ImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/image")
@Slf4j
@AllArgsConstructor
@Tag(name = "Image Query I/F")
public class ImageQueryController {
    private final ImageService imageService;

    @GetMapping("/page")
    @Operation(summary = "QUERY IMAGES BY PUBLIC")
    public BaseRes<Page<ImageVO>> pageByPublic(PageImgReq req,
                                               @RequestParam(required = false) Long userId,
                                               @RequestParam(required = false) Long spaceId) {
        return BaseRes.success(imageService.pagePublic(req, req.toQueryParams()
                .setSpaceId(spaceId)
                .setUserId(userId)));
    }

    @GetMapping("/page/s")
    @AuthPermission(mask = Permission.IMAGE_VIEW, id = ID.spaceId)
    @Operation(summary = "QUERY IMAGES BY SPACE")
    public BaseRes<Page<ImageVO>> pageBySpace(PageImgReq req,
                                              Long spaceId,
                                              @RequestParam(required = false) Long userId) {
        return BaseRes.success(imageService.pageAll(req, req.toQueryParams()
                .setSpaceId(spaceId)
                .setUserId(userId)));
    }

    @GetMapping("/page/u")
    @AuthLogged
    @Operation(summary = "QUERY IMAGES BY OPERATOR")
    public BaseRes<Page<ImageVO>> pageByOperator(PageImgReq req,
                                                 @RequestParam(required = false) Long spaceId) {
        return BaseRes.success(imageService.pageAll(req, req.toQueryParams()
                .setSpaceId(spaceId)));
    }

    @GetMapping("/tags")
    @Operation(summary = "LIST ALL IMAGE TAGS")
    public BaseRes<List<String>> listTags() {
        return BaseRes.success(imageService.getAllTags());
    }
}
