package io.github.hoooosi.imagehosting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@TableName(value = "tb_user")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class User extends BaseEntity {
    private String account;
    private String password;
    private String name;
    private String avatar;
    private String profile;
    private Role role;

    public enum Role {
        USER, ADMIN
    }
}