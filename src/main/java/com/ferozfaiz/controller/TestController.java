package com.ferozfaiz.controller;

import com.ferozfaiz.common.mptree.PathUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Feroz Faiz
 */

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return PathUtil.getPath("0001", 2000000);
//        return "Hello, World!";
    }
}