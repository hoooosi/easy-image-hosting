package io.github.hoooosi.imagehosting.controller;

import io.github.hoooosi.imagehosting.annotation.AuthLogged;
import io.github.hoooosi.imagehosting.dto.BaseRes;
import io.github.hoooosi.imagehosting.entity.User;
import io.github.hoooosi.imagehosting.modal.UserToken;
import io.github.hoooosi.imagehosting.service.UserService;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@Tag(name = "Auth I/F")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "AUTH")
    @GetMapping("/auth")
    @AuthLogged
    public BaseRes<UserInfoVO> auth() {
        return BaseRes.success(new UserInfoVO(userService.getById(SessionUtils.getUserId())));
    }
}
