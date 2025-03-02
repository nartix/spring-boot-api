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
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Feroz Faiz
 */
@SpringBootApplication
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
        for(int i=0; i<num; i++) {
            Category category = new Category();
            category.setName("Category " + i +1);
            category.setPath(categoryService.getPath("", 1, i+1));
            category.setDepth(categoryService.getDepth(category.getPath()));
            categoryRepository.save(category);
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

        logger.info("============ intToStr 1000000: {}",  categoryService.intToStr(1000000));
        logger.info("============ getDepth 0001000200030004: {}",  categoryService.getDepth("0001000200030004"));
        logger.info("============ getPath 0001: {}",  categoryService.getPath("0001", 2, 2));

        addData(40);

        logger.info("============ get last root node: {}",  categoryRepository.findTopByDepthOrderByPathDesc(1).map(Category::getPath).orElse("Not Found"));

        long executionTime = (System.currentTimeMillis() - startTime);
        logger.info("Time taken for calculation is : {} milliseconds", executionTime);
    }
}
