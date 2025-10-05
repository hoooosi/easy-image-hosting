package io.github.hoooosi.imagehosting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.imagehosting.vo.MemberVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberMapper extends MemberBaseMapper {
    default MemberVO query(Long id) {
        List<MemberVO> list = this.query(Wrappers
                .lambdaQuery(MemberVO.class)
                .eq(MemberVO::getId, id));
        return list.isEmpty() ? null : list.get(0);
    }

    List<MemberVO> query(@Param(Constants.WRAPPER) LambdaQueryWrapper<MemberVO> wrapper);

    Page<MemberVO> query(Page<MemberVO> page,
                         @Param(Constants.WRAPPER) LambdaQueryWrapper<MemberVO> wrapper);
}
