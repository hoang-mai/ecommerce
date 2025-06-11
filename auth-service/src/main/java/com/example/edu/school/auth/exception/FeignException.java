package com.example.edu.school.auth.exception;

import com.example.edu.school.auth.dto.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeignException extends RuntimeException{
    private BaseResponse<ExceptionDetail> response;
    public FeignException(String message, BaseResponse<ExceptionDetail> response) {
        super(message);
        this.response = response;
    }

    
}
