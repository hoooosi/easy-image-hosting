package io.github.hoooosi.imagehosting.utils;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import io.github.hoooosi.imagehosting.entity.ImageItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ImageItemUtils {
    private final IdentifierGenerator identifierGenerator;

    public ImageItem union(ImageItem imageItem, ImageFile imageFile) {
        return imageItem.setStatus(ImageItem.Status.SUCCESS)
                .setFileId(imageFile.getId())
                .setSize(imageFile.getSize())
                .setWidth(imageFile.getWidth())
                .setHeight(imageFile.getHeight())
                .setContentType(imageFile.getContentType())
                .setMd5(imageFile.getMd5());
    }
}
