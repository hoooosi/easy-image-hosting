package io.github.hoooosi.imagehosting.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.github.hoooosi.imagehosting.annotation.AuthLogged;
import io.github.hoooosi.imagehosting.dto.BaseRes;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.dto.LoginReq;
import io.github.hoooosi.imagehosting.dto.RegisterReq;
import io.github.hoooosi.imagehosting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@AllArgsConstructor
@Tag(name = "User I/F")
public class UserController {
    private final UserService userService;

    @Operation(summary = "REGISTER")
    @PostMapping("/register")
    public BaseRes<Void> register(@RequestBody RegisterReq req) {
        userService.register(req);
        return BaseRes.success();
    }

    @Operation(summary = "LOGIN")
    @PostMapping("/login")
    public BaseRes<Void> login(@RequestBody LoginReq req) {
        userService.login(req.getAccount(), req.getPassword());
        return BaseRes.success();
    }

    @Operation(summary = "LOGOUT")
    @PostMapping("/logout")
    @AuthLogged
    public BaseRes<Void> logout() {
        StpUtil.logout(SessionUtils.getUserIdOrThrow());
        return BaseRes.success();
    }
}
