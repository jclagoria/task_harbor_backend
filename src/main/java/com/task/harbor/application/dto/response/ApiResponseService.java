package com.task.harbor.application.dto.response;

public record ApiResponseService<T>(
    boolean success,
    String message,
    T data
) {
    public static <T> ApiResponseService<T> success(T data) {
        return new ApiResponseService<>(true, "Success", data);
    }
    
    public static <T> ApiResponseService<T> error(String message) {
        return new ApiResponseService<>(false, message, null);
    }
}