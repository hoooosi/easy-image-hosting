package io.github.hoooosi.imagehosting.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@TableName(value = "tb_image_item")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@ToString(callSuper = true)
public class ImageItem extends BaseEntity {
    private Long idxId;
    private Long fileId;
    private Long spaceId;
    private Status status;
    private String md5;
    private String contentType;
    private Long size;
    private Integer width;
    private Integer height;

    public enum Status {
        PROCESSING, SUCCESS, FAILED,
    }

}
