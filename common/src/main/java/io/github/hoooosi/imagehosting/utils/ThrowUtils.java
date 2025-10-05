package io.github.hoooosi.imagehosting.utils;

import io.github.hoooosi.imagehosting.exception.BusinessException;
import io.github.hoooosi.imagehosting.exception.ErrorCode;

public class ThrowUtils {

    public static void throwIf(boolean condition, RuntimeException e) {
        if (condition)
            throw e;
    }

    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }

    public static void throwIfNull(Object obj, ErrorCode errorCode) {
        throwIf(obj == null, errorCode);
    }

    public static void throwIfZero(int i, ErrorCode errorCode) {
        throwIf(i == 0, errorCode);
    }
}
