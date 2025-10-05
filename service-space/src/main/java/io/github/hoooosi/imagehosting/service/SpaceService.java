package io.github.hoooosi.imagehosting.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.dto.AddSpaceReq;
import io.github.hoooosi.imagehosting.dto.EditSpaceReq;
import io.github.hoooosi.imagehosting.dto.QuerySpaceReq;
import io.github.hoooosi.imagehosting.vo.SpaceVO;


public interface SpaceService extends IService<Space> {


    /**
     * Query space vo page
     */
    Page<SpaceVO> page(QuerySpaceReq req, LambdaQueryWrapper<SpaceVO> wrapper);

    /**
     * Query space vo by space id
     */
    SpaceVO get(Long sid);

    /**
     * Create space
     */
    void create(AddSpaceReq req);

    /**
     * Edit space
     */
    void edit(EditSpaceReq req);

    /**
     * Delete space by space id
     */
    void delete(Long sid);
}