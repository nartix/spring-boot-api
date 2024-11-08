package com.ferozfaiz.common.exception.mapper;

import com.ferozfaiz.common.exception.dto.ErrorDetailsDto;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConstraintViolationExceptionMapper extends BaseExceptionMapper<ConstraintViolationException> {

    private final MessageSource messageSource;

    public ConstraintViolationExceptionMapper(ErrorDetailsDto errorDetailsDto, MessageSource messageSource) {
        super(errorDetailsDto);
        this.messageSource = messageSource;
    }

    @Override
    public ProblemDetail toProblemDetail(ConstraintViolationException exception, String errorCode, HttpStatusCode status, String message, String details, LocalDateTime timestamp, List<Map<String, Object>> errors) {
        System.out.println("ConstraintViolationException" + exception.getConstraintViolations());

        List<Map<String, Object>> constraintErrors = new ArrayList<>();
        exception.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String messageTemplate = violation.getMessage();

            // Remove curly braces if present
            if (messageTemplate.startsWith("{") && messageTemplate.endsWith("}")) {
                messageTemplate = messageTemplate.substring(1, messageTemplate.length() - 1);
            }

            // Prepare message arguments based on constraint attributes (e.g., min, max)
            Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();

            String errorMessage = messageSource.getMessage(
                    messageTemplate,
                    null,
                    messageTemplate,  // Default message if key not found
                    LocaleContextHolder.getLocale()
            );

            // Manually replace placeholders like {min} and {max} with their values from attributes
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                assert errorMessage != null;
                if (errorMessage.contains(placeholder)) {
                    errorMessage = errorMessage.replace(placeholder, entry.getValue().toString());
                }
            }

            Map<String, Object> errorDetails = Map.of(
                "field", fieldName,
                "message", errorMessage,
                "rejectedValue", violation.getInvalidValue(),
                "code", violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()
            );

            constraintErrors.add(errorDetails);
        });

        return super.toProblemDetail(exception, errorCode, status, message, details, timestamp, constraintErrors);
    }
}
