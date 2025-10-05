package io.github.hoooosi.imagehosting.vo;

import io.github.hoooosi.imagehosting.entity.ImageIndex;
import io.github.hoooosi.imagehosting.entity.ImageItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImageVO extends ImageIndex {
    List<ImageItem> items;
    private Long creatorId;
    private String creatorName;
    private String creatorAvatar;
}
