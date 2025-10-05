package io.github.hoooosi.imagehosting.controller;

import io.github.hoooosi.imagehosting.dto.BaseRes;
import io.github.hoooosi.imagehosting.entity.User;
import io.github.hoooosi.imagehosting.service.UserService;
import io.github.hoooosi.imagehosting.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@Tag(name = "User I/F")
public class QueryController {
    private final UserService userService;

    @Operation(summary = "GET USER")
    @GetMapping
    public BaseRes<UserInfoVO> get(Long uid) {
        User entity = userService.lambdaQuery()
                .eq(User::getId, uid)
                .one();
        return BaseRes.success(new UserInfoVO(entity));
    }
}
