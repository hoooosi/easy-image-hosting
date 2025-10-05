package io.github.hoooosi.imagehosting.dto;

import io.github.hoooosi.imagehosting.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QueryImgReq extends PageReq {
    private String name;
    private String introduction;
    private List<String> tags;
}