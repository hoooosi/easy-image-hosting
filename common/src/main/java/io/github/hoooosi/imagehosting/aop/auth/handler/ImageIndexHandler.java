package io.github.hoooosi.imagehosting.aop.auth.handler;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.hoooosi.imagehosting.aop.auth.common.AbstractHandler;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.CacheNames;
import io.github.hoooosi.imagehosting.entity.ImageIndex;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.ImageIndexBaseMapper;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ImageIndexHandler extends AbstractHandler {

    private final ImageIndexBaseMapper imageIndexBaseMapper;
    private final SpaceHandler spaceHandler;

    @PostConstruct
    public void init() {
        this.setNextHandler(spaceHandler);
    }

    @Override
    public ID getID() {
        return ID.idxId;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.MASK_IMAGE_IDX, key = "#idxId+':'+T(io.github.hoooosi.imagehosting.utils.SessionUtils).getUserId()")
    public Long handlePermission(Long idxId) {
        // Check if the entity exists
        ImageIndex entity = imageIndexBaseMapper.selectOne(
                Wrappers.lambdaQuery(ImageIndex.class)
                        .select(ImageIndex::getSpaceId, ImageIndex::getUserId)
                        .eq(ImageIndex::getId, idxId)
        );
        ThrowUtils.throwIfNull(entity, ErrorCode.NOT_FOUND);

        // Operator is the owner of the image, pass
        if (entity.getUserId().equals(SessionUtils.getUserId()))
            return Long.MAX_VALUE;

        return super.invokeNext(entity.getSpaceId());
    }
}
