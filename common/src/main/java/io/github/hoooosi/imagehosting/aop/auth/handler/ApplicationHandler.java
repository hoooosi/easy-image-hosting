package io.github.hoooosi.imagehosting.aop.auth.handler;

import io.github.hoooosi.imagehosting.aop.auth.common.AbstractHandler;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.CacheNames;
import io.github.hoooosi.imagehosting.entity.Application;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.ApplicationBaseMapper;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ApplicationHandler extends AbstractHandler {
    private final SpaceHandler spaceHandler;
    private final ApplicationBaseMapper applicationBaseMapper;

    @PostConstruct
    public void init() {
        this.setNextHandler(spaceHandler);
    }

    @Override
    public ID getID() {
        return ID.applicationId;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.MASK_APPLICATION, key = "#applicationId+':'+T(io.github.hoooosi.imagehosting.utils.SessionUtils).getUserId()")
    public Long handlePermission(Long applicationId) {
        // Check if the entity exists
        Application entity = applicationBaseMapper.selectById(applicationId);
        ThrowUtils.throwIfNull(entity, ErrorCode.NOT_FOUND);

        if (entity.getType() == Application.Type.INVITE && entity.getUserId().equals(SessionUtils.getUserId())) {
            // Invitation to the operator, pass
            return Long.MAX_VALUE;
        }

        return super.invokeNext(entity.getSpaceId());
    }
}
