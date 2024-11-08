package com.ferozfaiz.common.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
public class DateTimeUtil {

    private static ZoneId ZONE_ID;

    @Value("${app.default.timezone:UTC}")
    private String defaultTimeZone; // Non-static to allow injection

    @PostConstruct
    public void init() {
        ZONE_ID = ZoneId.of(defaultTimeZone);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE_ID);
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE_ID);
    }

    public static OffsetDateTime getCurrentOffsetDateTime() {
        return OffsetDateTime.now(ZONE_ID);
    }
}
