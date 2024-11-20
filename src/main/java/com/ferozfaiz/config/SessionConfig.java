package com.ferozfaiz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.Duration;

@Configuration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 2000)
public class SessionConfig {


//    private final DataSource dataSource;
//
//    public SessionConfig(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        return dataSource;
//    }

//    @Value("${spring.session.timeout}")
//    private int maxInactiveIntervalInSeconds;
//
//    @Bean
//    public JdbcIndexedSessionRepository sessionRepository(DataSource dataSource, TransactionTemplate transactionTemplate) {
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//        JdbcIndexedSessionRepository sessionRepository = new JdbcIndexedSessionRepository(jdbcTemplate, transactionTemplate);
//        sessionRepository.setDefaultMaxInactiveInterval(Duration.ofSeconds(maxInactiveIntervalInSeconds));
//        return sessionRepository;
//    }
}
