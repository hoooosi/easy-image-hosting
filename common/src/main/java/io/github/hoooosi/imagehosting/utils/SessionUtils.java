package io.github.hoooosi.imagehosting.utils;

import cn.dev33.satoken.stp.StpUtil;
import io.github.hoooosi.imagehosting.entity.User;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.modal.UserToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionUtils {
    public static UserToken getUserToken() {
        if (!isLogged())
            return null;
        Object userObj = StpUtil.getSession().get(UserToken.class.getName());
        return (UserToken) userObj;
    }

    public static UserToken getUserOrThrow() {
        UserToken user = getUserToken();
        ThrowUtils.throwIfNull(user, ErrorCode.NOT_LOGGED);
        return user;
    }

    public static Long getUserId() {
        UserToken user = getUserToken();
        return user == null ? null : user.getId();
    }

    public static Long getUserIdOrThrow() {
        Long uid = getUserId();
        ThrowUtils.throwIfNull(uid, ErrorCode.NOT_LOGGED);
        return uid;
    }

    public static boolean isAdmin() {
        return getUserToken() != null && getUserToken().getRole() == User.Role.ADMIN;
    }

    public static boolean isLogged() {
        return StpUtil.isLogin();
    }
}
