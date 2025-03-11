package com.ferozfaiz.common.tree.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Feroz Faiz
 */
public class PathUtilTest {
    // Use default constructor (NumConv(36,BASE36) and stepLength=4)
    private final PathUtil pathUtil = new PathUtil();

    @Test
    void testIntToStrAndStrToInt() {
        long original = 123456789L;
        String str = pathUtil.intToStr(original);
        long back = pathUtil.strToInt(str);
        Assertions.assertEquals(original, back);
    }

    @Test
    void testGetBasePath() {
        String path = "ABCDWXYZ";
        // For stepLength = 4, depth 1 returns first 4 characters.
        String result = pathUtil.getBasePath(path, 1);
        Assertions.assertEquals("ABCD", result);

        // When depth * stepLength exceeds path length, whole path is returned.
        result = pathUtil.getBasePath(path, 3);
        Assertions.assertEquals("ABCDWXYZ", result);
    }

    @Test
    void testGetPath() {
        // Given a parent path with one segment
        String parentPath = "ABCD";
        // newstep=1 converts to "1" then pads to "0001", so full path should be "ABCD0001"
        String childPath = pathUtil.getPath(parentPath, 2, 1);
        Assertions.assertEquals("ABCD0001", childPath);
    }

    @Test
    void testGetDepth() {
        // For a valid path with three segments (each 4 characters)
        String path = "000100020003";
        int depth = pathUtil.getDepth(path);
        Assertions.assertEquals(3, depth);

        // Path length not a multiple of stepLength should throw an exception
        String invalidPath = "0001000";
        Assertions.assertThrows(IllegalArgumentException.class, () -> pathUtil.getDepth(invalidPath));
    }

    @Test
    void testIsAncestor() {
        String ancestor = "0001";
        String descendant = "00010002";
        // Valid ancestor case
        Assertions.assertTrue(pathUtil.isAncestor(ancestor, descendant));

        // Non-ancestor because prefix does not match
        Assertions.assertFalse(pathUtil.isAncestor("0002", descendant));

        // If descendant is not longer than ancestor, returns false
        Assertions.assertFalse(pathUtil.isAncestor(ancestor, "0001"));
    }

    @Test
    void testRebasePath() {
        // Given an old path "ABCD0001" and new parent "WXYZ", the new path should combine "WXYZ" with the last segment "0001"
        String oldPath = "ABCD0001";
        String newParent = "WXYZ";
        String result = pathUtil.rebasePath(oldPath, newParent);
        Assertions.assertEquals("WXYZ0001", result);

        // If new parent is empty, only the last segment is returned
        result = pathUtil.rebasePath(oldPath, "");
        Assertions.assertEquals("0001", result);
    }

    @Test
    void testGetMaxStepLength() {
        // Maximum number for stepLength of 4 with radix 36 should be 36^4
        long expected = (long) Math.pow(36, 4);
        long max = pathUtil.getMaxStepLength();
        Assertions.assertEquals(expected, max);
    }

    @Test
    void testGetFirstAncestor() {
        // For path "000100020003", the first segment is "0001"
        String path = "000100020003";
        String firstAncestor = pathUtil.getFirstAncestor(path);
        Assertions.assertEquals("0001", firstAncestor);

        // Passing an empty path should throw an exception.
        Assertions.assertThrows(IllegalArgumentException.class, () -> pathUtil.getFirstAncestor(""));
    }

    @Test
    void testGetPathByDepth() {
        // For path "000100020003", the second segment is "0002"
        String path = "000100020003";
        String segment = pathUtil.getPathByDepth(path, 2);
        Assertions.assertEquals("0002", segment);

        // Requesting a segment beyond the length should throw an exception.
        Assertions.assertThrows(IllegalArgumentException.class, () -> pathUtil.getPathByDepth(path, 4));
    }
}
