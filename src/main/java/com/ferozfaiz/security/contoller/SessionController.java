package com.ferozfaiz.security.contoller;

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

@RestController
@RequestMapping("/api/v1/session")
public class SessionController {

    private final JdbcIndexedSessionRepository sessionRepository;

    public SessionController(JdbcIndexedSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @GetMapping("/create")
    public ResponseEntity<String> createSession(HttpServletRequest request) {
        SessionIdGenerator sessionIdGenerator = UuidSessionIdGenerator.getInstance();
        HttpSession session = request.getSession(true);
        session.setAttribute("principal_name", "ferozfaiz");
//        return ResponseEntity.ok(session.getId());
        return ResponseEntity.ok(sessionIdGenerator.generate());
    }
}
