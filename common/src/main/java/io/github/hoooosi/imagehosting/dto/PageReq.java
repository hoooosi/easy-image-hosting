package io.github.hoooosi.imagehosting.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageReq implements Serializable {
    private int current = 1;
    private int size = 10;
    private boolean asc = true;
}
