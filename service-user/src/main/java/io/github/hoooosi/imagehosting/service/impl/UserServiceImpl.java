package io.github.hoooosi.imagehosting.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.imagehosting.entity.User;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.UserBaseMapper;
import io.github.hoooosi.imagehosting.modal.UserToken;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import io.github.hoooosi.imagehosting.dto.RegisterReq;
import io.github.hoooosi.imagehosting.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserBaseMapper, User> implements UserService {

    // Salt, used for password encryption
    public static final String SALT = "simple_picture";

    @Override
    public void register(RegisterReq req) {
        String account = req.getAccount();
        String password = req.getPassword();

        // Check account repeat
        ThrowUtils.throwIf(this.lambdaQuery()
                .eq(User::getAccount, account)
                .exists(), ErrorCode.ACCOUNT_REPEAT);

        // Create user entity and save
        User user = new User()
                .setAccount(account)
                .setPassword(this.getEncryptPassword(password))
                .setName(req.getName())
                .setRole(User.Role.USER);
        ThrowUtils.throwIf(!this.save(user), ErrorCode.DATA_SAVE_ERROR);

        // Record login state
        this.login(user);
    }

    @Override
    public void login(String account, String password) {
        // Encrypt password
        String encryptPassword = this.getEncryptPassword(password);

        // Check user exist
        User user = this.lambdaQuery()
                .eq(User::getAccount, account)
                .one();
        ThrowUtils.throwIfNull(user, ErrorCode.ACCOUNT_NOT_FOUND);

        // Check password, throw error if not match
        ThrowUtils.throwIf(!user.getPassword().equals(encryptPassword), ErrorCode.PASSWORD_ERROR);

        // Record login state
        this.login(user);
    }

    /**
     * Encrypt user password
     *
     * @param val User password
     * @return Encrypted password
     */
    private String getEncryptPassword(String val) {
        return DigestUtil.md5Hex(SALT + val);
    }

    /**
     * Login user, record login state
     *
     * @param user User entity
     */
    private void login(User user) {
        UserToken userToken = new UserToken(user);
        StpUtil.login(userToken.getId());
        StpUtil.getSession().set(UserToken.class.getName(), userToken);
    }
}