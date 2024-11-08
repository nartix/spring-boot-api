package com.ferozfaiz.common.exception.dto;

import org.springframework.http.HttpStatusCode;

public record ErrorDetailsDto(String errorCode, String messageCode, HttpStatusCode status) {
}
