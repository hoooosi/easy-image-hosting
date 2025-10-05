package io.github.hoooosi.imagehosting.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseRes<T> implements Serializable {

    private int code = 200;
    private String message = "Ok";
    private T data;

    public static BaseRes<Void> success() {
        return new BaseRes<>();
    }

    public static <T> BaseRes<T> success(String msg) {
        BaseRes<T> response = new BaseRes<>();
        response.setMessage(msg);
        return response;
    }

    public static <T> BaseRes<T> success(T data) {
        BaseRes<T> response = new BaseRes<>();
        response.setData(data);
        return response;
    }

    public static BaseRes<?> error(int code, String message) {
        BaseRes<?> response = new BaseRes<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
