package io.github.hoooosi.imagehosting.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterReq implements Serializable {
    @Size(min = 3, max = 20)
    @Pattern(regexp = "\\S+")
    private String name;
    @Size(min = 4, max = 20)
    @Pattern(regexp = "\\S+")
    private String account;
    @Size(min = 8, max = 20)
    @Pattern(regexp = "\\S+")
    private String password;
}
