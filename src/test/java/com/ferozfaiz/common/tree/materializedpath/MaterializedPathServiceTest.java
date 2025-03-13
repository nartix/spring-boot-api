
package com.ferozfaiz.common.tree.materializedpath;

import com.ferozfaiz.common.tree.category.Category;
import com.ferozfaiz.common.tree.category.CategoryRepository;
import com.ferozfaiz.common.tree.category.CategoryService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Feroz Faiz
 */

@SpringBootTest
@Transactional
public class MaterializedPathServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setup() {
        // Clean up repository before each test
        categoryRepository.deleteAll();
    }

    @Test
    public void testAddRoot() {
        Category root = new Category("Root Category");
        Category savedRoot = categoryService.addRoot(root);
        Assertions.assertNotNull(savedRoot.getPath(), "Root path should not be null");
        Assertions.assertEquals(1, savedRoot.getDepth(), "Root depth should be 1");
        Assertions.assertEquals(0, savedRoot.getNumChild(), "Initial numChild should be 0");
    }

    @Test
    public void testAddChildAndSibling() {
        // Create a root and add a child
        Category root = categoryService.addRoot(new Category("Root"));
        Category child = categoryService.addChild(root, new Category("Child 1"));
        Assertions.assertEquals(root.getDepth() + 1, child.getDepth(), "Child depth should be root depth + 1");

        // Verify parent's numChild is incremented
        Category updatedRoot = categoryRepository.findByPath(root.getPath()).orElse(null);
        Assertions.assertNotNull(updatedRoot, "Updated root should be present");
        Assertions.assertEquals(1, updatedRoot.getNumChild(), "Root should have 1 child");

        // Add a sibling to the child
        Category sibling = categoryService.addSibling(child, new Category("Child 2"));
        Assertions.assertEquals(child.getDepth(), sibling.getDepth(), "Sibling depth should be same as child depth");
        updatedRoot = categoryRepository.findByPath(root.getPath()).orElse(null);
        Assertions.assertEquals(2, updatedRoot.getNumChild(), "Root should now have 2 children");
    }

    @Test
    public void testDeleteNode() {
        // Create a tree structure: root with two children and a grandchild under first child
        Category root = categoryService.addRoot(new Category("Root for Delete"));
        Category child1 = categoryService.addChild(root, new Category("Child 1"));
        Category child2 = categoryService.addChild(root, new Category("Child 2"));
        Category grandChild = categoryService.addChild(child1, new Category("Grandchild"));

        // Delete child1 and verify its subtree is removed
        categoryService.delete(child1);
        Assertions.assertTrue(categoryRepository.findByPath(child1.getPath()).isEmpty(), "Child must be removed");
        Assertions.assertTrue(categoryRepository.findByPath(grandChild.getPath()).isEmpty(), "Grandchild must be removed");

        // Root should now only have one child remaining
        Category updatedRoot = categoryRepository.findByPath(root.getPath()).orElse(null);
        Assertions.assertNotNull(updatedRoot, "Root should be present");
        Assertions.assertEquals(1, updatedRoot.getNumChild(), "Root should have 1 child remaining");
    }

    @Test
    public void testMoveNode() {
        // Create two separate roots and a child to be moved from one root to the other
        Category root1 = categoryService.addRoot(new Category("Root1"));
        Category root2 = categoryService.addRoot(new Category("Root2"));
        Category child1 = categoryService.addChild(root1, new Category("Child1"));

        // Move child1 from root1 to root2
        categoryService.move(child1, root2);
        Category movedChild = categoryRepository.findById(child1.getId()).orElse(null);
        Assertions.assertNotNull(movedChild, "Moved child should still exist");
        Assertions.assertEquals(root2.getDepth() + 1, movedChild.getDepth(), "Moved child depth should be new parent depth + 1");

        // Verify parent's numChild: root1 should have 0 and root2 should have 1
        Category updatedRoot1 = categoryRepository.findById(root1.getId()).orElse(null);
        Assertions.assertNotNull(updatedRoot1, "Old parent must exist");
        Assertions.assertEquals(0, updatedRoot1.getNumChild(), "Old parent should have 0 children");

//        Category updatedRoot2 = categoryRepository.findById(root2.getId()).orElse(null);
//        Assertions.assertNotNull(updatedRoot2, "New parent must exist");
//        Assertions.assertEquals(1, updatedRoot2.getNumChild(), "New parent should have 1 child");
    }

    @Test
    public void testAddDataScenario() {
        // Simulate the addData logic from TestRunnerApplication using 2 roots each with children and grandchildren
        for (int i = 1; i <= 2; i++) {
            Category root = categoryService.addRoot(new Category("Category " + i));
            for (int j = 1; j <= 2; j++) {
                Category child = categoryService.addChild(root, new Category("Category " + i + "." + j));
                for (int k = 1; k <= 2; k++) {
                    categoryService.addChild(child, new Category("Category " + i + "." + j + "." + k));
                }
            }
        }
        // Verify there are 2 roots
        long rootCount = categoryRepository.findByPathStartingWith("")
                .stream()
                .filter(c -> c.getDepth() == 1)
                .count();
        Assertions.assertEquals(2, rootCount, "There should be 2 root nodes");
    }
}