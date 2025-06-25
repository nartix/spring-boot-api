package com.ferozfaiz.api.security;

import com.ferozfaiz.common.util.DateTimeUtil;
import com.ferozfaiz.security.session.SpringSession;
import com.ferozfaiz.security.session.SpringSessionRepository;
import com.ferozfaiz.security.user.UserRepository;
import com.ferozfaiz.security.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.session.SessionIdGenerator;
import org.springframework.session.UuidSessionIdGenerator;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${spring.data.rest.basePath}/auth/sessions")
public class SessionController {

//    private final JdbcIndexedSessionRepository sessionRepository;
//
//    public SessionController(JdbcIndexedSessionRepository sessionRepository) {
//        this.sessionRepository = sessionRepository;
//    }
//

    @Autowired
    private UserRepository userRepository;

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

    @PutMapping("/update")
    public ResponseEntity<?> updateSession(@RequestBody @Valid Map<String, Object> sessionData) {
        String sessionId = (String) sessionData.get("sessionId");
        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.badRequest().body("sessionId is required.");
        }

        Optional<SpringSession> optionalSession = springSessionRepository.findBySessionId(sessionId);
        if (optionalSession.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SpringSession session = optionalSession.get();

        // Update session fields
        if (sessionData.containsKey("ipAddress")) {
            session.setIpAddress((String) sessionData.get("ipAddress"));
        }
        if (sessionData.containsKey("urlPath")) {
            session.setUrlPath((String) sessionData.get("urlPath"));
        }
        if (sessionData.containsKey("userAgent")) {
            session.setUserAgent((String) sessionData.get("userAgent"));
        }

        // Update last access time
        long currentTimeMillis = Instant.now().toEpochMilli();
        session.setLastAccessTime(currentTimeMillis);

        // Update expiry time
        session.setExpiryTime(currentTimeMillis + session.getMaxInactiveInterval() * 1000);

        SpringSession updatedSession =  springSessionRepository.save(session);

        // Update user's last activity if userId is present
        if (session.getUserId() != null) {
            Optional<User> optionalUser = userRepository.findById(session.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setLastActivity(DateTimeUtil.getCurrentOffsetDateTime());
                userRepository.save(user);
            }
        }

        return ResponseEntity.ok(updatedSession);
    }
}
