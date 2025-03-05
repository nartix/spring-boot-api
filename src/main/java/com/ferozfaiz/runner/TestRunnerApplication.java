package com.ferozfaiz.runner;

import com.ferozfaiz.common.tree.category.Category;
import com.ferozfaiz.common.tree.category.CategoryRepository;
import com.ferozfaiz.common.tree.category.CategoryService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Feroz Faiz
 */
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
        SessionAutoConfiguration.class
})
@ComponentScan(basePackages =
        "com.ferozfaiz"
)
public class TestRunnerApplication implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;


    Logger logger = LoggerFactory.getLogger(TestRunnerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TestRunnerApplication.class, args);
    }

    public void addData(int num) {
        for(int i=1; i<=num; i++) {
            Category rootNode = categoryService.addRoot(new Category("Category " + i));
            for(int j=1; j<=10; j++) {
                Category childNode = new Category("Category " + i + "." + j);
                categoryService.addChild(rootNode, childNode);
//                for(int k=1; k<=num; k++) {
//                    Category grandChildNode = new Category("Category " + i + "." + j + "." + k);
//                    categoryService.addChild(childNode, grandChildNode);
//                }
            }
        }
    }

    @Override
    public void run(String... args) {
        long startTime = (System.currentTimeMillis());

//        System.out.println(" ============== String: " + numConvService.intToStr(1000000,36));
//        System.out.println(" ============== Number: " + numConvService.strToInt("LFLS", 36));


//        logger.info("========= Base Path: {}",  mpNodeService.getBasePath("0001000200030004", 1));
//        logger.info("========= Path: {}", mpNodeService.getPath("", 1, 1000000));
//        logger.info("============ test: {}",  String.format("%" + 4 + "s", "2"));

        logger.info("============ intToStr 1000000: {}",  categoryService.getPathUtil().intToStr(1000000));
        logger.info("============ getDepth 0001000200030004: {}",  categoryService.getPathUtil().getDepth("0001000200030004"));
        logger.info("============ getPath 0001: {}",  categoryService.getPathUtil().getPath("0001", 2, 2));

        addData(40);

//        logger.info("============ get last root node: {}",  categoryRepository.findTopByDepthOrderByPathDesc(1).map(Category::getPath).orElse("Not Found"));
        logger.info("============ get last root node: {}", categoryRepository.findTopByDepthOrderByPathDesc(1)
        .map(category -> "Path: " + category.getPath() + ", Name: " + category.getName() + ", Depth: " + category.getDepth())
        .orElse("Not Found"));

        long executionTime = (System.currentTimeMillis() - startTime);
        logger.info("Time taken for calculation is : {} milliseconds", executionTime);
    }
}
