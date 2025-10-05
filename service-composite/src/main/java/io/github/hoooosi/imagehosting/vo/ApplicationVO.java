package io.github.hoooosi.imagehosting.vo;
import io.github.hoooosi.imagehosting.entity.Application;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationVO extends Application {
    private String spaceName;
    private String userName;
    private String userAvatar;
}