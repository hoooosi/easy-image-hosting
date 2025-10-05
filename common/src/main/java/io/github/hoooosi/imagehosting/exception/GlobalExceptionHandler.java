package io.github.hoooosi.imagehosting.exception;

import cn.dev33.satoken.exception.NotPermissionException;
import io.github.hoooosi.imagehosting.dto.BaseRes;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(NotPermissionException.class)
    public BaseRes<?> notPermissionExceptionHandler(NotPermissionException e) {
        return BaseRes.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public BaseRes<?> businessExceptionHandler(BusinessException e) {
        return BaseRes.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(PSQLException.class)
    public BaseRes<?> PSQLExceptionHandler(PSQLException e) {
        log.error("{}", e);
        return BaseRes.error(45000, e.getMessage());
    }
}
