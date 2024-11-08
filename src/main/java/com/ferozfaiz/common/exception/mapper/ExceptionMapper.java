package com.ferozfaiz.common.exception.mapper;


import com.ferozfaiz.common.exception.dto.ErrorDetailsDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Feroz Faiz
 */
public interface ExceptionMapper<T extends Throwable> {
    default ProblemDetail toProblemDetail(T exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
    }

    default ProblemDetail toProblemDetail(T exception, String errorCode, HttpStatusCode status, String message, String details, LocalDateTime timestamp, List<Map<String, Object>> errors) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(message != null ? message : exception.getMessage());
        problemDetail.setDetail(details);
        problemDetail.setProperty("errorCode", errorCode);
        problemDetail.setProperty("timestamp", timestamp.toString());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    ErrorDetailsDto ErrorDetailsDto();
}
