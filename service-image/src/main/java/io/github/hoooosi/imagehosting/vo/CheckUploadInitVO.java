package io.github.hoooosi.imagehosting.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class CheckUploadInitVO {
    private boolean uploaded;
    private Long idxId;
    private Long itemId;
    private String url;
    private Map<String, String> formData;
    private Long expireAt;
}
