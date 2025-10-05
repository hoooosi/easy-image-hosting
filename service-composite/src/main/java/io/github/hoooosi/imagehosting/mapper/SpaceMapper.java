package io.github.hoooosi.imagehosting.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.imagehosting.vo.SpaceVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpaceMapper extends SpaceBaseMapper {

    default SpaceVO query(Long id, Long uid) {
        List<SpaceVO> list = this.query(Wrappers
                .lambdaQuery(SpaceVO.class)
                .eq(SpaceVO::getId, id), uid);
        return list.isEmpty() ? null : list.get(0);
    }

    List<SpaceVO> query(@Param(Constants.WRAPPER) LambdaQueryWrapper<SpaceVO> wrapper, @Param("userId") Long userId);

    Page<SpaceVO> query(Page<SpaceVO> page,
                        @Param(Constants.WRAPPER) Wrapper<SpaceVO> wrapper,
                        @Param("userId") Long userId);
}
