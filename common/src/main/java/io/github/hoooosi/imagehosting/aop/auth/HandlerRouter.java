package io.github.hoooosi.imagehosting.aop.auth;


import io.github.hoooosi.imagehosting.aop.auth.common.Handler;
import io.github.hoooosi.imagehosting.aop.auth.common.ID;
import io.github.hoooosi.imagehosting.aop.auth.handler.ApplicationHandler;
import io.github.hoooosi.imagehosting.aop.auth.handler.ImageIndexHandler;
import io.github.hoooosi.imagehosting.aop.auth.handler.MemberHandler;
import io.github.hoooosi.imagehosting.aop.auth.handler.SpaceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Routing to the appropriate Handler based on the incoming ID type
 */
@Component
public class HandlerRouter {
    private final List<Handler> HANDLERS;

    @Autowired
    public HandlerRouter(MemberHandler memberHandler,
                         ApplicationHandler applyHandler,
                         ImageIndexHandler imageIndexHandler,
                         SpaceHandler spaceHandler) {
        HANDLERS = List.of(memberHandler, applyHandler, spaceHandler, imageIndexHandler);
    }

    public Long handle(Long id, ID idType) {
        for (Handler handler : HANDLERS) {
            if (handler.getID() == idType)
                return handler.handlePermission(id);
        }
        return 0L;
    }
}
