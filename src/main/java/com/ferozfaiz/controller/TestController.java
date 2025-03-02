//package com.ferozfaiz.controller;
//
//import com.ferozfaiz.common.mptree.TreePathService;
//import com.ferozfaiz.common.mptree.test.CategoryRepository;
//import jakarta.persistence.EntityManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author Feroz Faiz
// */
//
//@RestController
//public class TestController {
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @Autowired
//    private TreePathService treePathService;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @GetMapping("/test")
//    @Transactional
//    public String test() {
//        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//
//
////        Category category = new Category();
////        category.setName("Test Category2");
//////        category.setDepth(0);
//////        category.setPath(treePathService.getPath("", 0));
//////        category.setNumChild(0);
////        Category.addRoot(entityManager, category, treePathService);
//////        categoryRepository.save(category);
//
//
//        return treePathService.getPath("", 36);
////        return "Hello, World!";
//    }
//}