package io.github.hoooosi.imagehosting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.hoooosi.imagehosting.entity.Member;
import io.github.hoooosi.imagehosting.dto.EditMemberReq;
import io.github.hoooosi.imagehosting.vo.MemberVO;


import java.util.List;

public interface MemberService extends IService<Member> {

    /**
     * Query member vo list
     */
    List<MemberVO> list(Long sid);

    /**
     * Edit member
     */
    void edit(EditMemberReq req);

    /**
     * Remove member
     */
    void remove(Long mid);

    /**
     * Exit space
     */
    void exit(Long sid);


}