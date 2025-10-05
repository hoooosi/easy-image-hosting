package io.github.hoooosi.imagehosting.aop.auth;


import io.github.hoooosi.imagehosting.aop.auth.common.AbstractHandler;
import io.github.hoooosi.imagehosting.aop.auth.common.Handler;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Routing to the appropriate Handler based on the incoming ID type
 */
@Component
public class HandlerRouter {
    @Resource
    private List<AbstractHandler> HANDLERS;

    public Long handle(Long id, ID idType) {
        for (Handler handler : HANDLERS) {
            if (handler.getID() == idType)
                return handler.handlePermission(id);
        }
        return 0L;
    }
}
