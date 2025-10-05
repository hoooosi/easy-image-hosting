package io.github.hoooosi.imagehosting.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class EditImgReq implements Serializable {
    private Long idxId;
    private String name;
    private String introduction;
    private List<String> tags;
}