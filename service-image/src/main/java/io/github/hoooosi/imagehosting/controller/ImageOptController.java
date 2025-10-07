package io.github.hoooosi.imagehosting.controller;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.hoooosi.imagehosting.annotation.AuthLogged;
import io.github.hoooosi.imagehosting.annotation.AuthPermission;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.Permission;
import io.github.hoooosi.imagehosting.dto.BaseRes;
import io.github.hoooosi.imagehosting.dto.CheckUploadInitReq;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import io.github.hoooosi.imagehosting.entity.ImageIndex;
import io.github.hoooosi.imagehosting.entity.ImageItem;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.utils.ImageUtils;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import io.github.hoooosi.imagehosting.dto.EditImgReq;
import io.github.hoooosi.imagehosting.service.ImageService;
import io.github.hoooosi.imagehosting.vo.CheckUploadInitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/image")
@Slf4j
@AllArgsConstructor
@Tag(name = "Image Opt I/F")
public class ImageOptController {

    private final ImageService imageService;

    @PostMapping("/upload/{spaceId}")
    @Operation(summary = "UPLOAD IMAGE(SIGN)")
    @AuthPermission(mask = Permission.IMAGE_UPLOAD, id = ID.spaceId)
    public BaseRes<CheckUploadInitVO> upload(@PathVariable Long spaceId,
                                             @RequestBody CheckUploadInitReq req) {

        return BaseRes.success(imageService.upload(req, spaceId));
    }

    @PutMapping
    @Operation(summary = "EDIT IMAGE")
    @AuthPermission(mask = Permission.IMAGE_EDIT, id = ID.idxId)
    public BaseRes<Void> edit(@RequestBody EditImgReq req) {
        ThrowUtils.throwIf(!imageService.update(Wrappers
                .lambdaUpdate(ImageIndex.class)
                .eq(ImageIndex::getId, req.getIdxId())
                .set(ImageIndex::getName, req.getName())
                .set(ImageIndex::getIntroduction, req.getIntroduction())
                .set(ImageIndex::getTags, req.getTags())), ErrorCode.OPERATION_ERROR);
        return BaseRes.success();
    }

    @PostMapping("/convert")
    @Operation(summary = "CONVERT IMAGE FORMAT")
    @AuthPermission(mask = Permission.IMAGE_EDIT, id = ID.idxId)
    public BaseRes<Void> convert(Long idxId, String contentType) {
        ThrowUtils.throwIfNull(ImageUtils.getFormatName(contentType), ErrorCode.NOT_FOUND);
        imageService.convert(idxId, contentType);
        return BaseRes.success();
    }

    @DeleteMapping("/{idxId}")
    @Operation(summary = "DELETE IMAGE BATCH")
    @AuthLogged
    @AuthPermission(mask = Permission.IMAGE_DELETE, id = ID.idxId)
    public BaseRes<Void> delete(@PathVariable Long idxId) {
        imageService.delete(idxId);
        return BaseRes.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "DELETE IMAGE BATCH")
    @AuthLogged
    public BaseRes<Void> deleteBatch(@RequestParam List<Long> ids) {
        imageService.deleteBatch(ids);
        return BaseRes.success();
    }
}
