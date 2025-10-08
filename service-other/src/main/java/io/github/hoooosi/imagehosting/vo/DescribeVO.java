package io.github.hoooosi.imagehosting.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DescribeVO {
    private String describe;
    private List<String> tags;
}
