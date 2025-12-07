package com.planitsquare.subject.global.common.handler;

import com.planitsquare.subject.global.common.dto.ApiResponse;
import com.planitsquare.subject.global.common.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(ApiException ex) {
        return ApiResponse.failedOf(ex);
    }
}
