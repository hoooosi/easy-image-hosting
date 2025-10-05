package io.github.hoooosi.imagehosting.controller;


import io.github.hoooosi.imagehosting.annotation.AuthPermission;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.Permission;
import io.github.hoooosi.imagehosting.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/image")
@Slf4j
@AllArgsConstructor
@Tag(name = "Image View I/F")
public class ImageViewController {
    private final ImageService imageService;

    @GetMapping("/view/{itemId}")
    @AuthPermission(mask = Permission.IMAGE_VIEW, id = ID.itemId)
    @Operation(summary = "GET IMAGE VIEW URL")
    public RedirectView getViewUrl(@PathVariable Long itemId) {
        return new RedirectView(imageService.generateTemporaryLink(itemId, false));
    }

    @GetMapping("/view/thumbnail/{itemId}")
    @AuthPermission(mask = Permission.IMAGE_VIEW, id = ID.itemId)
    @Operation(summary = "GET IMAGE THUMBNAIL VIEW URL")
    public RedirectView getThumbnailViewUrl(@PathVariable Long itemId) {
        return new RedirectView(imageService.generateTemporaryLink(itemId, true));
    }
}
