package io.github.hoooosi.imagehosting.controller;

import io.github.hoooosi.imagehosting.annotation.AuthPermission;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.Permission;
import io.github.hoooosi.imagehosting.dto.BaseRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@Slf4j
@AllArgsConstructor
@Tag(name = "AI I/F")
public class AiController {


    @GetMapping
    @Operation(summary = "DESCRIBE IMAGE")
    @AuthPermission(mask = Permission.IMAGE_VIEW, id = ID.idxId)
    public BaseRes<String> describe(Long idxId) {
        return BaseRes.success("TODO");
    }
}
