package com.ferozfaiz.common.exception;

import com.ferozfaiz.common.exception.dto.ErrorDetailsDto;
import com.ferozfaiz.common.exception.exception.AuthenticationFailedException;
import com.ferozfaiz.common.exception.exception.ResourceNotFoundException;
import com.ferozfaiz.common.exception.mapper.ConstraintViolationExceptionMapper;
import com.ferozfaiz.common.exception.mapper.ExceptionMapper;
import com.ferozfaiz.common.exception.mapper.GenericExceptionMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Feroz Faiz
 */
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    private final Map<Class<? extends Throwable>, ExceptionMapper<? extends Throwable>> exceptionMappers = new HashMap<>();
//
//    public GlobalExceptionHandler() {
//        // Register exception mappers here
//        exceptionMappers.put(NotFoundException.class, new NotFoundExceptionMapper());
//        exceptionMappers.put(ValidationException.class, new ValidationExceptionMapper());
//    }
//
//    @ExceptionHandler(ApplicationException.class)
//    public ResponseEntity<ErrorResponseDto> handleApplicationException(ApplicationException ex) {
//        ExceptionMapper<ApplicationException> mapper = (ExceptionMapper<ApplicationException>) exceptionMappers.get(ex.getClass());
//
//        if (mapper != null) {
//            ErrorResponseDto response = mapper.toResponse(ex);
//            return ResponseEntity.status(getHttpStatus(ex)).body(response);
//        } else {
//            return ResponseEntity.status(500).body(new ErrorResponseDto("UNKNOWN_ERROR", "An unexpected error occurred"));
//        }
//    }
//
//    private HttpStatus getHttpStatus(ApplicationException ex) {
//        if (ex instanceof NotFoundException) return HttpStatus.NOT_FOUND;
//        if (ex instanceof ValidationException) return HttpStatus.BAD_REQUEST;
//        return HttpStatus.INTERNAL_SERVER_ERROR;
//    }
//}

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Map<Class<? extends Throwable>, ExceptionMapper<? extends Throwable>> exceptionMappers = new HashMap<>();

    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
        // Register exception mappers here
//        exceptionMappers.put(NotFoundException.class, new NotFoundExceptionMapper());
//        exceptionMappers.put(ValidationException.class, new ValidationExceptionMapper());
        exceptionMappers.put(ResourceNotFoundException.class, new GenericExceptionMapper<>());
        exceptionMappers.put(AuthenticationFailedException.class,
                new GenericExceptionMapper<AuthenticationFailedException>(
                        new ErrorDetailsDto("AUTHENTICATION_FAILED", null, HttpStatusCode.valueOf(401)
                        )
                )
        );
        exceptionMappers.put(DataIntegrityViolationException.class,
                new GenericExceptionMapper<>(
                        new ErrorDetailsDto("DATA_INTEGRITY_VIOLATION", "error.data.integrity.violation", HttpStatusCode.valueOf(409)
                        )
                )
        );
        exceptionMappers.put(org.springframework.data.rest.webmvc.ResourceNotFoundException.class,
                new GenericExceptionMapper<>(
                        new ErrorDetailsDto("RESOURCE_NOT_FOUND", "error.resource.not.found", HttpStatusCode.valueOf(404)
                        )
                )
        );
//        exceptionMappers.put(MethodArgumentNotValidException.class, new MethodArgumentNotValidExceptionMapper(new ErrorDetailsDto("VALIDATION_FAILED", "error.validation.failed", HttpStatusCode.valueOf(400))));
        exceptionMappers.put(ConstraintViolationException.class,
                new ConstraintViolationExceptionMapper(
                        new ErrorDetailsDto("VALIDATION_FAILED", "error.validation.failed", HttpStatusCode.valueOf(400)
                        ), messageSource
                )
        );

        // Register conversion exception mappers for invalid ID format errors
        exceptionMappers.put(MethodArgumentTypeMismatchException.class,
                new GenericExceptionMapper<>(
                        new ErrorDetailsDto("INVALID_VALUE", "error.invalid.value", HttpStatusCode.valueOf(400)
                        )
                )
        );
        exceptionMappers.put(ConversionFailedException.class,
                new GenericExceptionMapper<>(
                        new ErrorDetailsDto("INVALID_VALUE", "error.invalid.value", HttpStatusCode.valueOf(400)
                        )
                )
        );
//        exceptionMappers.put(IllegalArgumentException.class,
//                new GenericExceptionMapper<>(new ErrorDetailsDto("INVALID_VALUE", "error.invalid.value", HttpStatusCode.valueOf(400))));
        exceptionMappers.put(InvalidDataAccessApiUsageException.class,
                new GenericExceptionMapper<>(
                        new ErrorDetailsDto(
                                "INVALID_SORT",                 // your external error code
                                "error.invalid.sort",           // your i18n message key
                                HttpStatusCode.valueOf(400)     // HTTP 400 Bad Request
                        )
                )
        );
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<Object> handleAllExceptions(Throwable ex, WebRequest request, HttpServletRequest httpServletRequest) {
        ExceptionMapper<Throwable> mapper = (ExceptionMapper<Throwable>) exceptionMappers.get(ex.getClass());

        ProblemDetail body;

        if (mapper != null) {
            ErrorDetailsDto errorDetailsDto = mapper.ErrorDetailsDto();
            String errorMessage = Optional.ofNullable(errorDetailsDto.messageCode())
                    .map(code -> messageSource.getMessage(
                            code,
                            null,
                            "An Error Occurred.",  // Default message if key not found
                            LocaleContextHolder.getLocale()
                    ))
                    .orElseGet(ex::getMessage);
            body = mapper.toProblemDetail(ex, errorDetailsDto.errorCode(), errorDetailsDto.status(), errorMessage, request.getDescription(false), LocalDateTime.now(), null);
        } else {
            body = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "An unexpected error occurred");
            body.setProperty("errorCode", "UNKNOWN_ERROR");
        }

        return new ResponseEntity<>(body, null, body.getStatus());
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, @Nonnull HttpHeaders headers, @Nonnull HttpStatusCode statusCode, @Nonnull WebRequest request) {
        if (body instanceof ProblemDetail problemDetail) {
            problemDetail.setProperty("error", problemDetail.getTitle());
            problemDetail.setProperty("timestamp", new Date());
            if (request instanceof ServletWebRequest) {
                problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest()
                        .getRequestURI());
            }
        }
        return new ResponseEntity<>(body, headers, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @Nonnull HttpHeaders headers, @Nonnull HttpStatusCode status, @Nonnull WebRequest request) {
        List<Map<String, Object>> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("field", fieldError.getField());
            errorDetails.put("message", fieldError.getDefaultMessage());
            errorDetails.put("rejectedValue", fieldError.getRejectedValue());
            errorDetails.put("code", fieldError.getCode());
            errors.add(errorDetails);
        });

        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, "Validation failed");
        body.setProperty("errorCode", "VALIDATION_FAILED");
        body.setProperty("timestamp", LocalDateTime.now().toString());
        body.setProperty("errors", errors);
        return new ResponseEntity<>(body, headers, status);

    }
}