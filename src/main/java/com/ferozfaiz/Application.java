package com.ferozfaiz;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.TimeZone;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Value("${app.default.timezone:UTC}")
	private String defaultTimeZone;

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone(defaultTimeZone));
	}
}
