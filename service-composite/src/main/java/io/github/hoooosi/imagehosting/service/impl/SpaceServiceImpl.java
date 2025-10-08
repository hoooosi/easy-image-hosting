package io.github.hoooosi.imagehosting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.imagehosting.constant.CacheNames;
import io.github.hoooosi.imagehosting.entity.Member;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.MemberBaseMapper;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.dto.AddSpaceReq;
import io.github.hoooosi.imagehosting.dto.EditSpaceReq;
import io.github.hoooosi.imagehosting.mapper.SpaceMapper;
import io.github.hoooosi.imagehosting.service.SpaceService;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceService {
    private final MemberBaseMapper memberBaseMapper;
    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public void create(AddSpaceReq req) {
        Long userId = SessionUtils.getUserId();
        // Synchronize create space by user id, prevent create multiple space at the
        // same time
        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            Space space = new Space()
                    .setName(req.getName())
                    .setMaxSize(req.getMaxSize())
                    .setPublicPermissionMask(req.getPublicPermissionMask())
                    .setMemberPermissionMask(req.getMemberPermissionMask());
            ThrowUtils.throwIf(!this.save(space), ErrorCode.DATA_SAVE_ERROR);
            ThrowUtils.throwIfZero(memberBaseMapper.insert(new Member()
                    .setSpaceId(space.getId())
                    .setUserId(userId)
                    .setPermissionMask(Long.MAX_VALUE)), ErrorCode.DATA_SAVE_ERROR);
        }
    }

    @Override
    public void edit(EditSpaceReq req) {
        Space entity = this.getById(req.getSpaceId());
        ThrowUtils.throwIfNull(entity, ErrorCode.NOT_FOUND);
        ThrowUtils.throwIf(req.getMaxSize() < entity.getTotalSize(), ErrorCode.OPERATION_ERROR);
        ThrowUtils.throwIf(!this.lambdaUpdate()
                .eq(Space::getId, req.getSpaceId())
                .set(Space::getName, req.getName())
                .set(Space::getMaxSize, req.getMaxSize())
                .set(Space::getPublicPermissionMask, req.getPublicPermissionMask())
                .set(Space::getMemberPermissionMask, req.getMemberPermissionMask())
                .update(), ErrorCode.OPERATION_ERROR);
        rocketMQTemplate.convertAndSend(CacheNames.MASK_SPACE, req.getSpaceId());
    }

    @Override
    @Transactional
    public void delete(Long sid) {
        this.removeById(sid);
    }
}