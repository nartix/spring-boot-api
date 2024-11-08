package com.ferozfaiz.common.exception.mapper;


import com.ferozfaiz.common.exception.dto.ErrorDetailsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public abstract class BaseExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {
    protected ErrorDetailsDto errorDetailsDto;

    protected BaseExceptionMapper(ErrorDetailsDto errorDetailsDto) {
        this.errorDetailsDto = errorDetailsDto;
    }

    protected BaseExceptionMapper() {
        this.errorDetailsDto = new ErrorDetailsDto("UNKNOWN_ERROR", "error.unexpected", HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    public ErrorDetailsDto ErrorDetailsDto() {
        return errorDetailsDto;
    }

    public void setErrorDetailsDto(ErrorDetailsDto errorDetailsDto) {
        this.errorDetailsDto = errorDetailsDto;
    }

    public ProblemDetail toProblemDetail(T exception, String errorCode, HttpStatusCode status, String message, String details, LocalDateTime timestamp, List<Map<String, Object>> errors) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(message);
        problemDetail.setDetail(details);
        problemDetail.setProperty("errorCode", errorCode);
        problemDetail.setProperty("timestamp", timestamp.toString());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }
}
