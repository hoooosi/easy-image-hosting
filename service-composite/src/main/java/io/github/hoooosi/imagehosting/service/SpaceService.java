package io.github.hoooosi.imagehosting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.dto.AddSpaceReq;
import io.github.hoooosi.imagehosting.dto.EditSpaceReq;


public interface SpaceService extends IService<Space> {

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