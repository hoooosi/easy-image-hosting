package io.github.hoooosi.imagehosting.dto;

import io.github.hoooosi.imagehosting.entity.Application;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ApplicationHandleReq implements Serializable {
    @NotNull
    private Long applicationId;
    @NotNull
    private Application.Status status;
}