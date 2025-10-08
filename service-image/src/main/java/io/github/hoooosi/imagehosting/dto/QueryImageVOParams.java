package io.github.hoooosi.imagehosting.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class QueryImageVOParams {
    private String keyword;
    private List<String> tags;
    private Long userId;
    private Long spaceId;
    private boolean asc;
}
