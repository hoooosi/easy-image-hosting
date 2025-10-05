package io.github.hoooosi.imagehosting.vo;

import io.github.hoooosi.imagehosting.entity.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class MemberVO extends Member implements Serializable {
    private String name;
    private String account;
    private String avatar;
}
