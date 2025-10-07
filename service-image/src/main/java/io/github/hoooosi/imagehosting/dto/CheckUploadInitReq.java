package io.github.hoooosi.imagehosting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckUploadInitReq {
    @NotNull
    private String filename;
    @NotNull
    private String contentType;
    @NotNull
    private String md5;
    @NotNull
    private int size;
}
