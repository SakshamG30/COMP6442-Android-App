package com.example.g11_group_application.Service_layer;

import org.json.JSONException;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 19-April-2024
 * Represents a node in an AVL tree. Each node holds a key and associated data, along with pointers to left and right child nodes,
 * and maintains a height for balancing the tree.
 */
public class AVLNode<T extends Comparable<T>> {
    T id;  // The key for the AVL tree to maintain ordering
    private NodeData data;  // Holds all attribute data
    AVLNode<T> left, right; // Pointers to the left and right children
    int height;  // Height of this node for balancing the AVL tree

    public AVLNode(T id, NodeData data) {
        this.id = id;
        this.data = data;
        this.height = 1;
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Recursively displays the tree structure in a visually appealing format, using indentation to indicate tree depth.
     * @param tabs The number of tabs to prepend to the display output for this node, indicating its depth in the tree.
     * @return A formatted string representing the subtree rooted at this node.
     */
    public String display(int tabs) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s (%s)\n", id, data.toJson().toString()));  // Display the current node in Json format

        // Append left child display if it exists
        if (left != null) {
            sb.append(repeatString("\t", tabs)).append("├─").append(left.display(tabs + 1));
        }

        // Append right child display if it exists
        if (right != null) {
            sb.append(repeatString("\t", tabs)).append("└─").append(right.display(tabs + 1));
        }

        return sb.toString();
    }

    private String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public NodeData getData() {
        return data;
    }

    public void setData(NodeData data) {
        this.data = data;
    }
}
