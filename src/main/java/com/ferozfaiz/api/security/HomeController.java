package com.ferozfaiz.api.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui.html";
    }
}
