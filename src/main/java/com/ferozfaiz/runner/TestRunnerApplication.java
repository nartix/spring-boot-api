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
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
        org.springframework.boot.autoconfigure.session.SessionAutoConfiguration.class,
//        org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class,

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
            for(int j=1; j<=5; j++) {
                Category childNode = new Category("Category " + i + "." + j);
//                childNode.setPath(categoryService.getPathUtil().getPath(rootNode.getPath(), 2, j));
//                childNode.setDepth(2);
//                childNode.setNumChild(0);
//                rootNode.setNumChild(rootNode.getNumChild() + 1);
//                categoryRepository.save(rootNode);
//                categoryRepository.save(childNode);
                categoryService.addChild(rootNode, childNode);

                for(int k=1; k<=5; k++) {
                    categoryService.addChild(childNode, new Category("Category " + i + "." + j + "." + k));
                }
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

//        logger.info("============ intToStr 1000000: {}",  categoryService.getPathUtil().intToStr(1000000));
//        logger.info("============ getDepth 0001000200030004: {}",  categoryService.getPathUtil().getDepth("0001000200030004"));
//        logger.info("============ getPath 0001: {}",  categoryService.getPathUtil().getPath("0001", 2, 2));

//        logger.info("============ get path at depth ?: {}",  categoryService.getPathUtil().getPathByDepth("00010002000A", 3));
//        logger.info("============ get strToInt \"\" test: {}",  categoryService.getPathUtil().strToInt(""));
//        logger.info("============ get strToInt null test: {}",  categoryService.getPathUtil().strToInt(null));

        addData(5);
        categoryService.addSibling(categoryRepository.findByPath("0001").orElse(null), new Category("Category Sibling Test Root"));
        categoryService.addSibling(categoryRepository.findByPath("00010001").orElse(null), new Category("Category Sibling Test Root/Child"));

        // delete root node and all its children
        categoryService.delete(categoryRepository.findByPath("0004").orElse(null));
        // delete child node and all its children
        categoryService.delete(categoryRepository.findByPath("00010006").orElse(null));

        // move node in the same level
//        categoryService.move(categoryRepository.findByPath("00020001").orElse(null), categoryRepository.findByPath("00020002").orElse(null));

        // move node to a different level
//        categoryService.move(categoryRepository.findByPath("00020001").orElse(null), categoryRepository.findByPath("00030001").orElse(null));

        // move a root node to a different level
//        categoryService.move(categoryRepository.findByPath("0001").orElse(null), categoryRepository.findByPath("0005").orElse(null));

        // move a child node to the root level
//        categoryService.move(categoryRepository.findByPath("00010001").orElse(null), categoryRepository.findByPath("0006").orElse(null));

        // move a third level node to the root level
//        categoryService.move(categoryRepository.findByPath("000100020003").orElse(null), categoryRepository.findByPath("0006").orElse(null));

        // move a root node to its child node, will throw an exception
//        categoryService.move(categoryRepository.findByPath("0001").orElse(null), categoryRepository.findByPath("00010001").orElse(null));

        // move a root node to the third level
//        categoryService.move(categoryRepository.findByPath("0002").orElse(null), categoryRepository.findByPath("000100020003").orElse(null));

//        // testing path overflow
//        // JPA will throws DataIntegrityViolationException when path exceeds 255 characters
//        categoryRepository.findByPath("0006").ifPresent(category -> {
//            category.setPath("000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001");
//            category.setDepth(63);
//            categoryRepository.save(category);
//        });
//        categoryService.move(categoryRepository.findByPath("000500050005").orElse(null), categoryRepository.findByPath("000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001000100010001").orElse(null));


        Category root1 = categoryService.addRoot(new Category("Root1"));
        Category root2 = categoryService.addRoot(new Category("Root2"));
        Category child1 = categoryService.addChild(root1, new Category("Child1"));

        // Move child1 from root1 to root2
        categoryService.move(child1, root2);

//       String pathtest =  categoryService.getPathUtil().getPath()

//        logger.info("============ get last child node: {}",  categoryRepository.findTopByPathStartingWithAndDepthOrderByPathDesc("0009", 2).map(Category::getPath).orElse("Not Found"));

//        logger.info("============ get last root node: {}",  categoryRepository.findTopByDepthOrderByPathDesc(1).map(Category::getPath).orElse("Not Found"));
//        logger.info("============ get last root node: {}", categoryRepository.findTopByDepthOrderByPathDesc(1)
//        .map(category -> "Path: " + category.getPath() + ", Name: " + category.getName() + ", Depth: " + category.getDepth())
//        .orElse("Not Found"));

        long executionTime = (System.currentTimeMillis() - startTime);
        logger.info("Time taken for calculation is : {} milliseconds", executionTime);
    }
}
