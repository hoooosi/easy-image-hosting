package io.github.hoooosi.imagehosting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.hoooosi.imagehosting.entity.User;
import io.github.hoooosi.imagehosting.dto.RegisterReq;

public interface UserService extends IService<User> {

    /**
     * User register
     */
    void register(RegisterReq req);

    /**
     * User login
     */
    void login(String account, String password);
}
