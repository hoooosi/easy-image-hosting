package io.github.hoooosi.imagehosting.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.hoooosi.imagehosting.dto.PageReq;
import io.github.hoooosi.imagehosting.entity.Application;
import io.github.hoooosi.imagehosting.vo.ApplicationVO;

import java.util.List;

public interface ApplicationService extends IService<Application> {
    /**
     * Query application
     */
    List<ApplicationVO> list(LambdaQueryWrapper<ApplicationVO> wrapper);

    /**
     * Apply to join space
     */
    void apply(Long spaceId);

    /**
     * Invite user to join space
     */
    void invite(Long spaceId, Long userId);

    /**
     * Handle application
     */
    void handle(Long applicationId, Application.Status status, Application.HandleType handleType);

}
