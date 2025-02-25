package com.ferozfaiz.runner;

import com.ferozfaiz.common.mptree.NumConvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Feroz Faiz
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.ferozfaiz.common.mptree")
public class TestRunnerApplication implements CommandLineRunner {

    @Autowired
    private NumConvService numConvService;


    public static void main(String[] args) {
        SpringApplication.run(TestRunnerApplication.class, args);
    }

    @Override
    public void run(String... args) {

        System.out.println(" ============== String: " + numConvService.intToStr(1000000,36));
        System.out.println(" ============== Number: " + numConvService.strToInt("LFLS", 36));
    }
}
