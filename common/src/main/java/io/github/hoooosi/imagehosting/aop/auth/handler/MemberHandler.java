package io.github.hoooosi.imagehosting.aop.auth.handler;

import io.github.hoooosi.imagehosting.aop.auth.common.AbstractHandler;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.CacheNames;
import io.github.hoooosi.imagehosting.entity.Member;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.MemberBaseMapper;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MemberHandler extends AbstractHandler {

    private final MemberBaseMapper memberBaseMapper;
    private final SpaceHandler spaceHandler;

    @PostConstruct
    public void init() {
        this.setNextHandler(spaceHandler);
    }

    @Override
    public ID getID() {
        return ID.memberId;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.MASK_MEMBER, key = "#mid+':'+T(io.github.hoooosi.imagehosting.utils.SessionUtils).getUserId()")
    public Long handlePermission(Long memberId) {
        // Check if the entity exists
        Member entity = memberBaseMapper.selectById(memberId);
        ThrowUtils.throwIfNull(entity, ErrorCode.NOT_FOUND);

        return super.invokeNext(entity.getSpaceId());
    }
}