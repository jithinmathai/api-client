package com.test.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String msg;
    private T data;
    
    public static <T> ApiResponse<T> success(String msg, T data) {
        return ApiResponse.<T>builder()
            .code(0)
            .msg(msg)
            .data(data)
            .build();
    }
    
    public static <T> ApiResponse<T> failure(int code, String msg) {
        return ApiResponse.<T>builder()
            .code(code)
            .msg(msg)
            .build();
    }
    
    public static <T> ApiResponse<T> failure(int code, String msg, T data) {
        return ApiResponse.<T>builder()
            .code(code)
            .msg(msg)
            .data(data)
            .build();
    }
    
    @JsonIgnore
    public boolean isSuccess() {
        return code == 0;
    }
}
