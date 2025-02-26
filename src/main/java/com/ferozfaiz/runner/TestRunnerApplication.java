package com.ferozfaiz.runner;

import com.ferozfaiz.common.mptree.MPService;
import com.ferozfaiz.common.mptree.NumConvService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private EntityManager entityManager;

    @Autowired
    private NumConvService numConvService;

    @Autowired
    private MPService mpService;

    Logger logger = LoggerFactory.getLogger(TestRunnerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TestRunnerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        long startTime = (System.currentTimeMillis());

//        System.out.println(" ============== String: " + numConvService.intToStr(1000000,36));
//        System.out.println(" ============== Number: " + numConvService.strToInt("LFLS", 36));


        logger.info("========= Base Path: {}",  mpService.getBasePath("0001000200030004", 1));
        logger.info("========= Path: {}", mpService.getPath("", 1, 1000000));

        logger.info("============ test: {}",  String.format("%" + 4 + "s", "2"));

        long executionTime = (System.currentTimeMillis() - startTime);
        logger.info("Time taken for calculation is : {} milliseconds", executionTime);
    }
}
