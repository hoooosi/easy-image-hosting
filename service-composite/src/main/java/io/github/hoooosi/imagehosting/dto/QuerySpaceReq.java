package io.github.hoooosi.imagehosting.dto;


import io.github.hoooosi.imagehosting.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuerySpaceReq extends PageReq {
    private String name;
    private Long mask;
}
