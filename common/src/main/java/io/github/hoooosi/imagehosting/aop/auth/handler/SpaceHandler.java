package io.github.hoooosi.imagehosting.aop.auth.handler;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.hoooosi.imagehosting.aop.auth.common.AbstractHandler;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.constant.CacheNames;
import io.github.hoooosi.imagehosting.entity.Member;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.MemberBaseMapper;
import io.github.hoooosi.imagehosting.mapper.SpaceBaseMapper;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SpaceHandler extends AbstractHandler {
    private final MemberBaseMapper memberBaseMapper;
    private final SpaceBaseMapper spaceBaseMapper;

    @Override
    public ID getID() {
        return ID.spaceId;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.MASK_SPACE, key = "#spaceId+':'+T(io.github.hoooosi.imagehosting.utils.SessionUtils).getUserId()")
    public Long handlePermission(Long spaceId) {
        // If the user is logged in, check whether he is a member of the space
        if (SessionUtils.isLogged()) {
            Member entity = memberBaseMapper.selectOne(
                    Wrappers.lambdaQuery(Member.class)
                            .eq(Member::getSpaceId, spaceId)
                            .eq(Member::getUserId, SessionUtils.getUserId())
            );

            // Return the member's permission mask if the member exists
            if (entity != null)
                return entity.getPermissionMask();
        }

        // The operator is not logged in, try to return the space public permission mask
        Space entity = spaceBaseMapper.selectById(spaceId);
        ThrowUtils.throwIfNull(entity, ErrorCode.NOT_FOUND);
        log.info("Space public permission mask: {}", entity.getPublicPermissionMask());
        return entity.getPublicPermissionMask();
    }
}