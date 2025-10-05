package io.github.hoooosi.imagehosting.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.imagehosting.entity.Member;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.MemberBaseMapper;
import io.github.hoooosi.imagehosting.dto.EditMemberReq;
import io.github.hoooosi.imagehosting.mapper.MemberMapper;
import io.github.hoooosi.imagehosting.service.MemberService;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import io.github.hoooosi.imagehosting.vo.MemberVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
    @Override
    public List<MemberVO> list(Long spaceId) {
        return baseMapper.query(Wrappers
                .lambdaQuery(MemberVO.class)
                .eq(MemberVO::getSpaceId, spaceId));
    }

    @Override
    public void edit(EditMemberReq req) {
        Long userId = SessionUtils.getUserIdOrThrow();
        ThrowUtils.throwIf(this.lambdaQuery()
                .eq(Member::getId, req.getMemberId())
                .eq(Member::getUserId, userId)
                .ne(Member::getUserId, userId) // Ensure
                .exists(), ErrorCode.NOT_FOUND);
        ThrowUtils.throwIf(!this.lambdaUpdate()
                .eq(Member::getId, req.getMemberId())
                .set(Member::getPermissionMask, req.getMask())
                .update(), ErrorCode.DATA_SAVE_ERROR);
    }

    @Override
    public void remove(Long memberId) {
        Long userId = SessionUtils.getUserIdOrThrow();

        ThrowUtils.throwIf(this.lambdaQuery()
                .eq(Member::getId, memberId)
                .eq(Member::getUserId, userId)
                .ne(Member::getPermissionMask, Long.MAX_VALUE)
                .exists(), ErrorCode.NOT_FOUND);

        ThrowUtils.throwIf(!this.removeById(memberId), ErrorCode.DATA_SAVE_ERROR);
    }

    @Override
    public void exit(Long sid) {
        Long userId = SessionUtils.getUserId();

        // Get member and check exist
        Member member = this.lambdaQuery()
                .select(Member::getId, Member::getPermissionMask)
                .eq(Member::getSpaceId, sid)
                .eq(Member::getUserId, userId)
                .one();
        ThrowUtils.throwIfNull(member, ErrorCode.NOT_FOUND);

        // Check user is owner, throw error if is
        ThrowUtils.throwIf(member.getPermissionMask().equals(Long.MAX_VALUE), ErrorCode.OPERATION_ERROR);

        // Remove member
        ThrowUtils.throwIf(!this.removeById(member.getId()), ErrorCode.OPERATION_ERROR);
    }
}
