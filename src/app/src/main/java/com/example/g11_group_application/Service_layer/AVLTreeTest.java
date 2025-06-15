package com.example.g11_group_application.Service_layer;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 8-May-2024
 * Tests the AVLTree class for insertion, deletion, searching, and rotation operations.
 */
public class AVLTreeTest {

    private AVLTree<Integer> avlTree;

    @Before
    public void setUp() {
        avlTree = new AVLTree<>();
    }

    @Test
    public void testInsertion() {
        avlTree.insert(3, new NodeData());
        avlTree.insert(2, new NodeData());
        avlTree.insert(1, new NodeData());

        assertTrue("AVL property should be maintained after insertions", avlTree.isAVLTree());
        assertEquals("Root should be 2 after right rotation", Integer.valueOf(2), avlTree.getRoot().id);

        avlTree.insert(4, new NodeData());
        avlTree.insert(5, new NodeData());

        assertTrue("AVL property should be maintained after more insertions", avlTree.isAVLTree());
        assertEquals("Root should be updated to 3 after left rotation", Integer.valueOf(2), avlTree.getRoot().id);
    }

    @Test
    public void testDeletion() {
        avlTree.insert(50, new NodeData());
        avlTree.insert(30, new NodeData());
        avlTree.insert(70, new NodeData());
        avlTree.insert(20, new NodeData());
        avlTree.insert(40, new NodeData());
        avlTree.insert(60, new NodeData());
        avlTree.insert(80, new NodeData());

        avlTree.delete(50); // Should adjust tree and potentially require rotations

        assertTrue("AVL property should be maintained after deletion", avlTree.isAVLTree());
        assertNotEquals("Root should not be 50 after deletion", Integer.valueOf(50), avlTree.getRoot().id);
    }

    @Test
    public void testSearch() {
        avlTree.insert(10, new NodeData());
        avlTree.insert(20, new NodeData());
        avlTree.insert(5, new NodeData());

        assertNotNull("Search should find existing element 10", avlTree.search(10));
        assertNull("Search should not find non-existent element 15", avlTree.search(15));
    }

    @Test
    public void testRotation() {
        // Manually triggering rotations by inserting elements that will unbalance the tree
        avlTree.insert(1, new NodeData());
        avlTree.insert(2, new NodeData());
        avlTree.insert(3, new NodeData()); // Triggers a left rotation

        assertEquals("Root should be 2 after rotation", Integer.valueOf(2), avlTree.getRoot().id);

        avlTree.insert(4, new NodeData());
        avlTree.insert(5, new NodeData()); // Triggers a right rotation on 3

        assertEquals("Root should still be 2, and right child should be 4", Integer.valueOf(4), avlTree.getRoot().right.id);
    }
}