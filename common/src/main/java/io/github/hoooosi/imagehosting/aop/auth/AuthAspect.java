package io.github.hoooosi.imagehosting.aop.auth;

import io.github.hoooosi.imagehosting.annotation.AuthLogged;
import io.github.hoooosi.imagehosting.annotation.AuthPermission;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.utils.SessionUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class AuthAspect {
    private final HandlerRouter handlerRouter;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Before("@annotation(authPermission)")
    public void authPermission(JoinPoint joinPoint, AuthPermission authPermission) {
        Long id = this.getId(joinPoint, authPermission);
        long target = authPermission.mask();
        Long mask = handlerRouter.handle(id,authPermission.id());
        ThrowUtils.throwIf((mask & target) != target, ErrorCode.NO_PERMISSION);
    }

    @Before("@annotation(authLogged)")
    public void authLogged(JoinPoint joinPoint, AuthLogged authLogged) {
        ThrowUtils.throwIf(!SessionUtils.isLogged(), ErrorCode.NOT_LOGGED);
    }

    private Long getId(JoinPoint joinPoint, AuthPermission authPermission) {
        String field = authPermission.id().name();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);

        EvaluationContext context = new MethodBasedEvaluationContext(null, method, args, parameterNameDiscoverer);
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        Long id;
        id = parser.parseExpression("#" + field).getValue(context, Long.class);
        if (id == null) id = parser.parseExpression("#req." + field).getValue(context, Long.class);
        ThrowUtils.throwIfNull(id, ErrorCode.PARAMS_ERROR);
        return id;
    }
}
