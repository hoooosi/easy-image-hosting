package io.github.hoooosi.imagehosting.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginReq implements Serializable {
    private String account;
    private String password;
}
