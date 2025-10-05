package io.github.hoooosi.imagehosting.vo;

import io.github.hoooosi.imagehosting.entity.User;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Data
@Accessors(chain = true)
public class UserInfoVO {
    private String account;
    private String name;
    private String avatar;
    private String profile;
    private User.Role role;
    private Long createTime;
    private Long updateTime;

    public UserInfoVO(User user) {
        this.account = user.getAccount();
        this.name = user.getName();
        this.avatar = user.getAvatar();
        this.profile = user.getProfile();
        this.role = user.getRole();
        this.createTime = user.getCreateTime();
        this.updateTime = user.getUpdateTime();
    }
}
