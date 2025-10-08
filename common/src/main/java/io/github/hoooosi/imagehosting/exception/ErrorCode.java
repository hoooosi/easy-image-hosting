package io.github.hoooosi.imagehosting.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(20000, "Ok"),

    PARAMS_ERROR(40000, "Params Error"),
    ACCOUNT_REPEAT(40001, "Account Repeat"),
    ACCOUNT_NOT_FOUND(40002, "Account Not Found"),
    PASSWORD_ERROR(40003, "Password Error"),
    NOT_LOGGED(40100, "Not Logged"),
    NO_AUTH(40101, "No Auth"),
    NO_PERMISSION(40102, "No Permission"),
    APPLICATION_EXISTS(400003, "Application already exists"),
    ILLEGAL_OPERATION(40004, "Illegal operation"),
    UNABLE_TO_OPERATE_MYSELF(40005, "Unable to operate myself"),
    ALREADY_IN_SPACE(40006, "Already in space"),
    NOT_FOUND(40400, "Not Found"),
    FORBIDDEN(40300, "Forbidden"),

    FORMAT_ALREADY_EXISTS(41002, "Format already exists"),
    UNSUPPORTED_MEDIA_TYPE(41500, "Unsupported Media Type"),
    INSUFFICIENT_CAPACITY(41001, "Insufficient remaining space"),

    SYSTEM_ERROR(50000, "System Error"),
    OPERATION_ERROR(50001, "Operation Error"),
    DATA_SAVE_ERROR(50002, "Data Save Error");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}