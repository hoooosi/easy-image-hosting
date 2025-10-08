package io.github.hoooosi.imagehosting.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ConvertMessage implements Serializable {
    private Long itemId;
    private String contentType;
    private String objectKey;
}
