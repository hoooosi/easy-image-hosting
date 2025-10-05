package io.github.hoooosi.imagehosting.modal;

import io.github.hoooosi.imagehosting.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserToken implements Serializable {
    private Long id;
    private String account;
    private User.Role role;

    public UserToken(User user) {
        this.id = user.getId();
        this.account = user.getAccount();
        this.role = user.getRole();
    }
}
