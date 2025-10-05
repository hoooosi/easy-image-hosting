package io.github.hoooosi.imagehosting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@TableName(value = "tb_application")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class Application extends BaseEntity {
    private Type type;
    private Long userId;
    private Long spaceId;
    private Status status;
    private HandleType handleType;
    private LocalDateTime handleTime;

    public enum Type {
        INVITE,
        APPLY
    }

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    public enum HandleType {
        AUTO,
        MANUAL
    }
}