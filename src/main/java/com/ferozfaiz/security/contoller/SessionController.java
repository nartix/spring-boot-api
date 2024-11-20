package com.ferozfaiz.security.contoller;

import com.ferozfaiz.security.session.SpringSession;
import com.ferozfaiz.security.session.SpringSessionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/session")
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

        String sessionId1 = sessionIdGenerator.generate();
        String sessionId2 = sessionIdGenerator.generate();

        SpringSession session = new SpringSession();
        session.setPrimaryId(sessionId1);
        session.setSessionId(sessionId1);
        session.setPrincipalName("ferozfaiz");


        springSessionRepository.save(session);

        Map<String, String> response = new HashMap<>();
        response.put("sessionId1", sessionId1);
        response.put("sessionId2", sessionId2);
        return ResponseEntity.ok(response);
    }
}
