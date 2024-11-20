package com.ferozfaiz.security.contoller;

import com.ferozfaiz.common.util.DateTimeUtil;
import com.ferozfaiz.security.session.SpringSession;
import com.ferozfaiz.security.session.SpringSessionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.session.SessionIdGenerator;
import org.springframework.session.UuidSessionIdGenerator;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${spring.data.rest.basePath}/session")
public class SessionController {

//    private final JdbcIndexedSessionRepository sessionRepository;
//
//    public SessionController(JdbcIndexedSessionRepository sessionRepository) {
//        this.sessionRepository = sessionRepository;
//    }
//
    @Autowired
    SpringSessionRepository springSessionRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createSession(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        SessionIdGenerator sessionIdGenerator = UuidSessionIdGenerator.getInstance();
        String primaryId = sessionIdGenerator.generate();
        String sessionId = sessionIdGenerator.generate();

        LocalDateTime now = DateTimeUtil.now();

        // Get the current time in milliseconds
        long creationTimeMillis = DateTimeUtil.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        int maxInactiveInterval = 30; // Default session timeout (30 minutes)
        long expiryTimeMillis = creationTimeMillis + (maxInactiveInterval * 1000);

        SpringSession session = new SpringSession();
        session.setPrimaryId(primaryId);
        session.setSessionId(sessionId);
        session.setCreationTime(creationTimeMillis);
        session.setLastAccessTime(creationTimeMillis);
        session.setMaxInactiveInterval(maxInactiveInterval);
        session.setExpiryTime(expiryTimeMillis);
        session.setPrincipalName(requestBody.getOrDefault("principalName", "guest"));

        springSessionRepository.save(session);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("creationTime", session.getCreationTime());
        response.put("expiryTime", session.getExpiryTime());
        response.put("maxInactiveInterval", session.getMaxInactiveInterval());

        return ResponseEntity.ok(response);
    }
}
