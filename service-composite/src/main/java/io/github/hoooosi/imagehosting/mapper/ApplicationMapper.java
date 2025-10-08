package io.github.hoooosi.imagehosting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.imagehosting.vo.ApplicationVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApplicationMapper extends ApplicationBaseMapper {
    default ApplicationVO query(Long id) {
        List<ApplicationVO> list = this.query(Wrappers
                .lambdaQuery(ApplicationVO.class)
                .eq(ApplicationVO::getId, id));
        return list.isEmpty() ? null : list.get(0);
    }

    List<ApplicationVO> query(@Param(Constants.WRAPPER) LambdaQueryWrapper<ApplicationVO> wrapper);

}
