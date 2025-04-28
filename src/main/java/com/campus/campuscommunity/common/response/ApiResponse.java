package com.campus.campuscommunity.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 JSON에 포함하지 않음
public class ApiResponse<T> {

    private int status;         // HTTP 상태 코드
    private String message;     // 응답 메시지
    private T data;             // 응답 데이터 (제네릭 타입)

    // 성공 응답 생성 메서드 (데이터 포함)
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(ResponseCode.SUCCESS.getStatus())
                .message(ResponseCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    // 성공 응답 생성 메서드 (데이터 없음)
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .status(ResponseCode.SUCCESS.getStatus())
                .message(ResponseCode.SUCCESS.getMessage())
                .build();
    }

    // 커스텀 성공 응답 생성 메서드
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(ResponseCode.SUCCESS.getStatus())
                .message(message)
                .data(data)
                .build();
    }

    // 오류 응답 생성 메서드
    public static <T> ApiResponse<T> error(ResponseCode code) {
        return ApiResponse.<T>builder()
                .status(code.getStatus())
                .message(code.getMessage())
                .build();
    }

    // 커스텀 오류 응답 생성 메서드
    public static <T> ApiResponse<T> error(ResponseCode code, String message) {
        return ApiResponse.<T>builder()
                .status(code.getStatus())
                .message(message)
                .build();
    }
}