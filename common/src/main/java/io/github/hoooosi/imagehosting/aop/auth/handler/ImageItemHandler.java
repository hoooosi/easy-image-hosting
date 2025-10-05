package io.github.hoooosi.imagehosting.aop.auth.handler;

import io.github.hoooosi.imagehosting.aop.auth.common.AbstractHandler;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.CacheNames;
import io.github.hoooosi.imagehosting.entity.ImageItem;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.ImageItemBaseMapper;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ImageItemHandler extends AbstractHandler {
    private final SpaceHandler spaceHandler;
    private final ImageItemBaseMapper imageItemBaseMapper;

    @PostConstruct
    public void init() {
        this.setNextHandler(spaceHandler);
    }

    @Override
    public ID getID() {
        return ID.itemId;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.MASK_IMAGE_ITEM, key = "#itemId+':'+T(io.github.hoooosi.imagehosting.utils.SessionUtils).getUserId()")
    public Long handlePermission(Long itemId) {
        ImageItem imageItem = imageItemBaseMapper.selectById(itemId);
        ThrowUtils.throwIfNull(imageItem, ErrorCode.NOT_FOUND);
        return this.nextHandler.handlePermission(imageItem.getSpaceId());
    }
}
