package com.ferozfaiz.config;

import com.ferozfaiz.security.jwt.config.JwtConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class AppConfig {
    @Bean
    public JwtConfig jwtConfig(
            @Value("${RSA_KEY_SECRET}") String rsaPrivateKey,
            @Value("${RSA_KEY_PUBLIC}") String rsaPublicKey,
            @Value("${JWT_TOKEN_VALIDITY}") long tokenValidity,
            @Value("${JWT_REFRESH_TOKEN_VALIDITY}") long refreshTokenValidity,
            @Value("${DOMAIN}") String issuer) {
        return new JwtConfig(rsaPrivateKey, rsaPublicKey, tokenValidity, refreshTokenValidity, issuer);
    }

//    @Bean
//    public MessageSource messageSource() {
//        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
//        messageSource.setBasename("messages");  // Load messages.properties
//        messageSource.setDefaultEncoding("UTF-8");
//        return messageSource;
//    }

//    @Bean
//    public MessageSource messageSource() {
//        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
//
//        // Set the base name of the messages properties file
//        messageSource.setBasename("classpath:messages");
//
//        // Set the default encoding to UTF-8
//        messageSource.setDefaultEncoding("UTF-8");
//
//        // Optional: Set cache duration if you want to reload messages without restarting the server
//         messageSource.setCacheSeconds(3600);
//
//        return messageSource;
//    }
//
//    @Bean
//    public LocalValidatorFactoryBean validator() {
//        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
//        bean.setValidationMessageSource(messageSource());
//        return bean;
//    }
}
