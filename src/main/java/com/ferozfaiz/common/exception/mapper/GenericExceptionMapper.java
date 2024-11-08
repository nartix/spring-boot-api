package com.ferozfaiz.common.exception.mapper;


import com.ferozfaiz.common.exception.dto.ErrorDetailsDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
*
* @author Feroz Faiz
* 
*/
public class GenericExceptionMapper<T extends Throwable> extends BaseExceptionMapper<T> {

    public GenericExceptionMapper() {
        super();
    }

    public GenericExceptionMapper(ErrorDetailsDto errorDetailsDto) {
        super(errorDetailsDto);
    }

    @Override
    public ProblemDetail toProblemDetail(T exception, String errorCode, HttpStatusCode status, String message, String details, LocalDateTime timestamp, List<Map<String, Object>> errors) {
        return super.toProblemDetail(exception, errorCode, status, message, details, timestamp, errors);
    }
}