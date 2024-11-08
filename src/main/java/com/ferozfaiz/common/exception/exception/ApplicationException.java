package com.ferozfaiz.common.exception.exception;

import com.ferozfaiz.common.exception.dto.ErrorDetailsDto;
import org.springframework.context.MessageSource;

/**
 * @author Feroz Faiz
 */
public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}


