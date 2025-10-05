package io.github.hoooosi.imagehosting.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.imagehosting.dto.PageReq;

public class PageUtils {
    public static <T> Page<T> of(PageReq req) {
        return new Page<T>(req.getCurrent(), req.getSize());
    }
}
