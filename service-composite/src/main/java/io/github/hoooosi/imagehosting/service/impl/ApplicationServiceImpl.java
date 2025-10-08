package io.github.hoooosi.imagehosting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.imagehosting.constant.Permission;
import io.github.hoooosi.imagehosting.entity.Application;
import io.github.hoooosi.imagehosting.entity.Member;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.*;
import io.github.hoooosi.imagehosting.service.ApplicationService;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import io.github.hoooosi.imagehosting.vo.ApplicationVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {
    private final MemberBaseMapper memberBaseMapper;
    private final SpaceMapper spaceMapper;
    private final MemberMapper memberMapper;

    @Override
    public List<ApplicationVO> list(LambdaQueryWrapper<ApplicationVO> wrapper) {
        return baseMapper.query(wrapper);
    }

    @Override
    public void apply(Long spaceId) {
        Long userId = SessionUtils.getUserIdOrThrow();

        // Check user exist in space
        ThrowUtils.throwIf(checkUserExistInSpace(spaceId, userId), ErrorCode.ALREADY_IN_SPACE);

        // Get space
        Space space = spaceMapper.selectById(spaceId);
        ThrowUtils.throwIfNull(space, ErrorCode.NOT_FOUND);

        // Check apply exist
        ThrowUtils.throwIf(this.exists(Wrappers
                .lambdaQuery(Application.class)
                .eq(Application::getSpaceId, spaceId)
                .eq(Application::getUserId, userId)
                .eq(Application::getStatus, Application.Status.PENDING)), ErrorCode.OPERATION_ERROR);

        // Create apply and save
        Application application = new Application()
                .setType(Application.Type.APPLY)
                .setSpaceId(spaceId)
                .setUserId(userId)
                .setStatus(Application.Status.PENDING);
        this.save(application);

        // Auto approve if space set auto approve
        if (Permission.has(space.getPublicPermissionMask(), Permission.SPACE_AUTO_APPROVE)) {
            log.info("Auto approve apply {} in space {}", application.getId(), spaceId);
            this.handle(application.getId(), Application.Status.APPROVED, Application.HandleType.AUTO);
        }
    }

    @Override
    public void invite(Long spaceId, Long userId) {
        // Check apply exist
        ThrowUtils.throwIf(checkUserExistInSpace(spaceId, userId), ErrorCode.OPERATION_ERROR);

        // Check invite exist
        ThrowUtils.throwIf(this.lambdaQuery().eq(Application::getSpaceId, spaceId)
                .eq(Application::getUserId, userId)
                .eq(Application::getStatus, Application.Status.PENDING)
                .exists(), ErrorCode.APPLICATION_EXISTS);

        // Create apply and save
        Application application = new Application()
                .setType(Application.Type.INVITE)
                .setSpaceId(spaceId)
                .setUserId(userId)
                .setStatus(Application.Status.PENDING);

        this.save(application);
    }

    @Override
    public void handle(Long applicationId, Application.Status status, Application.HandleType handleType) {
        // Get apply and check status
        Application application = this.lambdaQuery()
                .eq(Application::getId, applicationId)
                .one();
        ThrowUtils.throwIfNull(application, ErrorCode.NOT_FOUND);
        ThrowUtils.throwIf(!Application.Status.PENDING.equals(application.getStatus()), ErrorCode.OPERATION_ERROR);

        // Update db
        Space space = spaceMapper.selectById(application.getSpaceId());
        application.setStatus(status)
                .setHandleType(handleType);
        this.updateById(application);

        // Insert member into space if approved
        if (application.getStatus() == Application.Status.APPROVED) {
            Member member = new Member();
            member.setUserId(application.getUserId());
            member.setSpaceId(application.getSpaceId());
            member.setPermissionMask(space.getMemberPermissionMask());
            ThrowUtils.throwIfZero(memberMapper.insert(member), ErrorCode.OPERATION_ERROR);
        }
    }

    /**
     * Check user exist in space
     */
    private boolean checkUserExistInSpace(Long sid, Long uid) {
        return memberBaseMapper.exists(Wrappers.lambdaQuery(Member.class)
                .eq(Member::getSpaceId, sid)
                .eq(Member::getUserId, uid));
    }
}
