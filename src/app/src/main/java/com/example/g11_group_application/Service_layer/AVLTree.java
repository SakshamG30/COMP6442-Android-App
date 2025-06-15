package com.example.g11_group_application.Service_layer;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 19-April-2024
 * Implements an AVL Tree for managing sorted data efficiently. This class supports insertion, deletion, search
 * and modification operations while maintaining balance to ensure optimal performance.
 */

import com.example.g11_group_application.firebase_connection_DAO.FirestoreSchema;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class AVLTree<T extends Comparable<T>> {
    private AVLNode<T> root; // Root node of the Tree

    /**
     * Inserts a new node with a given id and data into the AVL Tree.
     * @param id The unique identifier for the new node.
     * @param data The data to be stored in the new node.
     */
    public void insert(T id, NodeData data) {
        root = insertRec(root, id, data);
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Method to recursively insert a new node into the subtree while balancing it.
     * @param node Current node in the recursion.
     * @param id The id of the new node.
     * @param data The data of the new node.
     * @return The new or modified subtree with the new node inserted.
     */
    private AVLNode<T> insertRec(AVLNode<T> node, T id, NodeData data) {
        if (node == null) {
            return new AVLNode<>(id, data);
        }

        int compareResult = id.compareTo(node.id);

        if (compareResult < 0) {
            node.left = insertRec(node.left, id, data);
        } else if (compareResult > 0) {
            node.right = insertRec(node.right, id, data);
        } else {
            // Duplicate keys not allowed
            node.setData(data);
        }

        return balanceNode(node);
    }

    /**
     * Removes a node with the specified id from the AVL Tree.
     * @param id The id of the node to remove.
     */
    public void delete(T id) {
        root = deleteRec(root, id);
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Method to recursively delete a node from the subtree while balancing it.
     * @param node Current node in the recursion.
     * @param id The id of the node to delete.
     * @return The new or modified subtree with the node removed.
     */
    private AVLNode<T> deleteRec(AVLNode<T> node, T id) {
        if (node == null) {
            return null;
        }

        int compareResult = id.compareTo(node.id);

        if (compareResult < 0) {
            node.left = deleteRec(node.left, id);
        } else if (compareResult > 0) {
            node.right = deleteRec(node.right, id);
        } else {
            // Node with only one child or no child
            if ((node.left == null) || (node.right == null)) {
                AVLNode<T> temp = node.left != null ? node.left : node.right;

                if (temp == null) {
                    node = null;
                } else {
                    node = temp;
                }
            } else {
                // Node with two children: Get the in-order predecessor (rightmost in left subtree)
                AVLNode<T> temp = maxDataNode(node.left);

                // Replace node's id and data with that of the in-order predecessor
                node.id = temp.id;
                node.setData(temp.getData());

                // Delete the in-order predecessor
                node.left = deleteRec(node.left, temp.id);
            }
        }

        if (node == null) {
            return node;
        }

        // Update height of the current node
        node.height = Math.max(height(node.left), height(node.right)) + 1;

        // Balance the tree
        return balanceNode(node);
    }

    /**
     * Searches for the existence of a node with the specified key.
     * @param key The key to search for.
     * @return true if the node exists, false otherwise.
     */
    public boolean find(T key) {
        return findRec(root, key) != null;
    }

    /**
     * Retrieves the node with the specified key.
     * @param key The key of the node to find.
     * @return The node with the specified key, or null if no such node exists.
     */
    public AVLNode<T> search(T key) {
        return findRec(root, key);
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Recursively finds a node with the specified key within the subtree.
     * @param node Current node in the recursion.
     * @param key The key to search for.
     * @return The node if found, or null.
     */
    private AVLNode<T> findRec(AVLNode<T> node, T key) {
        if (node == null) {
            return null;  // Key not found
        }
        if (key.compareTo(node.id) == 0) {
            return node;  // Key found
        } else if (key.compareTo(node.id) < 0) {
            return findRec(node.left, key);  // Search in the left subtree
        } else {
            return findRec(node.right, key);  // Search in the right subtree
        }
    }

    /**
     * Finds the node with the maximum key in the subtree rooted at a given node.
     * @param node The root node of the subtree.
     * @return The node with the maximum key in the subtree.
     */
    public AVLNode<T> maxDataNode(AVLNode<T> node) {
        AVLNode<T> current = node;
        while (current.right != null) {
            current = current.right;
        }
        return current;
    }

    /**
     * Finds the node with the minimum key in the subtree rooted at a given node.
     * @param node The root node of the subtree.
     * @return The node with the minimum key in the subtree.
     */
    public AVLNode<T> minDataNode(AVLNode<T> node) {
        AVLNode<T> current = node;

        while (current.left != null) {
            current = current.left;
        }

        return current;
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Balances the AVL Tree to ensure that it maintains optimal height and performance characteristics.
     * @param node The root node of the subtree to balance.
     * @return The balanced subtree.
     */
    private AVLNode<T> balanceNode(AVLNode<T> node) {
        if (node == null) {
            return null;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rightRotate(node);
        }

        // Left Right Case
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && getBalance(node.right) <= 0) {
            return leftRotate(node);
        }

        // Right Left Case
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    /**
     * Calculates the height of a given node in the AVL tree.
     * @param node The node whose height is to be calculated.
     * @return The height of the node.
     */
    private int height(AVLNode<T> node) {
        if (node == null) return 0;
        return node.height;
    }

    /**
     * Calculates the balance factor of a given node in the AVL tree.
     * @param node The node whose balance factor is to be calculated.
     * @return The balance factor of the node.
     */
    private int getBalance(AVLNode<T> node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Performs a right rotation on a node to maintain AVL tree balance.
     * @param y The node to rotate around.
     * @return The new root after rotation.
     */
    private AVLNode<T> rightRotate(AVLNode<T> y) {
        AVLNode<T> x = y.left;
        AVLNode<T> T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Performs a left rotation on a node to maintain AVL tree balance.
     * @param x The node to rotate around.
     * @return The new root after rotation.
     */
    private AVLNode<T> leftRotate(AVLNode<T> x) {
        AVLNode<T> y = x.right;
        AVLNode<T> T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Updates the data for a node with the specified ID.
     * @param id The ID of the node to update.
     * @param newData The new data to set in the node.
     * Throws NoSuchElementException if no node is found with the given ID.
     */
    public void updateNodeData(T id, NodeData newData) {
        AVLNode<T> node = search(id);
        if (node != null) {
            node.setData(newData);
        } else {
            throw new NoSuchElementException("No node found with the given ID to update.");
        }
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Updates the ID of an existing node, effectively moving the data to a new key within the tree.
     * @param oldId The current ID of the node.
     * @param newId The new ID for the node.
     * Throws NoSuchElementException if no node is found with the given ID.
     */
    public void updateNodeId(T oldId, T newId) {
        AVLNode<T> node = search(oldId);
        if (node != null) {
            NodeData data = node.getData();
            delete(oldId);        // Remove the old node
            insert(newId, data);  // Insert the new node with updated ID
        } else {
            throw new NoSuchElementException("No node found with the given ID to update.");
        }
    }

    /**
     * Provides a string representation of the entire tree in a structured format.
     * @return A string representing the structured view of the tree, or "Tree is empty" if the tree has no nodes.
     */
    public String display() throws JSONException {
        if (root == null) {
            return "Tree is empty";
        }
        return root.display(0);
    }


    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Serializes the entire AVL tree to a JSON object. Each node's data is converted to JSON and organized by node ID.
     * @return A JSONObject representing the tree.
     */
    public JSONObject serializeToJson() throws JSONException {
        JSONObject result = new JSONObject();
        flattenTreeToJson(root, result);
        return result;
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Recursively flattens the tree to a JSON object, with the id being mapped to the nodeData in Json Format.
     * @param node The current node in the recursion.
     * @param result The JSONObject being built up with the tree's data.
     */
    private void flattenTreeToJson(AVLNode<T> node, JSONObject result) throws JSONException {
        if (node == null) {
            return;
        }
        JSONObject nodeDataJson = node.getData().toJson(); //Convert the Node data to Json format

        // Put the current node data in the result JSON object
        result.put(String.valueOf(node.id), nodeDataJson);

        // Recursively serialize the left and right subtrees
        flattenTreeToJson(node.left, result);
        flattenTreeToJson(node.right, result);
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Rebuilds the AVL Tree from a JSON object. Each key-value pair in the JSON is assumed to represent a node.
     * @param json The JSON object representing the tree to be rebuilt.
     */
    public void deserializeFromJson(JSONObject json) throws JSONException {

        for (Iterator<String> it = json.keys(); it.hasNext(); ) {
            String key = it.next();
            JSONObject nodeDataJson = json.getJSONObject(key); // Use the id key to get the Node data in Json format.
            T id = (T) convertToComparable(key); // Method to convert JSON key to Comparable Type i.e. Int or String
            NodeData nodeData = NodeData.fromJson(nodeDataJson); //Convert the Json data to NodeData format

            this.insert(id, nodeData); // Insert nodes into the AVL tree
        }
    }

    /**
     * Converts a string key from the JSON object to the Comparable type used in the AVL tree.
     * @param key The string key to convert.
     * @return The comparable type suitable for use as an ID in the AVL tree.
     */
    private T convertToComparable(String key) {
        // Add logic to parse key to the correct type here
        if(key.matches("-?\\d+"))
            return (T) Integer.valueOf(key);
        return (T) key;
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 19-April-2024
     * Creates a NodeData object based on the specified Firestore collection and a variable number of attribute values.
     * This method organizes data into a NodeData object according to the fields defined in the FirestoreSchema enums,
     * allowing for dynamic creation of node data for various collections.
     *
     * @param collection The Firestore collection enum which determines the schema to use for creating NodeData.
     * @param values The values to be set in the NodeData object, expected to follow the order defined in the corresponding enum.
     * @return A fully populated NodeData object based on the provided values and collection schema.
     * @throws IllegalArgumentException if the provided collection type is not recognized.
     */
    public NodeData createNodeData(FirestoreSchema.FireStoreCollection collection, Object... values) {
        NodeData nodeData = new NodeData();

        switch (collection) {
            case ANCW_LOGIN_USER_MASTER:
                // Assuming `values` follow the order of attributes in the enum
                for (FirestoreSchema.LoginUserMaster field : FirestoreSchema.LoginUserMaster.values()) {
                    int ordinal = field.ordinal();
                    if (ordinal < values.length) {
                        nodeData.addAttribute(field.getAttributeName(), values[ordinal]);
                    }
                }
                break;
            case ANCW_ROLE_MASTER:
                for (FirestoreSchema.RoleMaster field : FirestoreSchema.RoleMaster.values()) {
                    int ordinal = field.ordinal();
                    if (ordinal < values.length) {
                        nodeData.addAttribute(field.getAttributeName(), values[ordinal]);
                    }
                }
                break;
            case ANCW_SECURITY_QUESTION_MASTER:
                for (FirestoreSchema.SecurityQuestionMaster field : FirestoreSchema.SecurityQuestionMaster.values()) {
                    int ordinal = field.ordinal();
                    if (ordinal < values.length) {
                        nodeData.addAttribute(field.getAttributeName(), values[ordinal]);
                    }
                }
                break;
            case ANCW_USER_SECURITY_QUESTION:
                for (FirestoreSchema.UserSecurityQuestion field : FirestoreSchema.UserSecurityQuestion.values()) {
                    int ordinal = field.ordinal();
                    if (ordinal < values.length) {
                        nodeData.addAttribute(field.getAttributeName(), values[ordinal]);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown collection type.");
        }

        return nodeData;
    }

    /**
     * Performs an in-order traversal of the AVL Tree and collects User data of a particular userID.
     * @return User filtered and extracted from the tree nodes.
     */
    public User getUser(T id) {
        User filterUser = null;
        for(User user: getUsers()){
            if(convertToComparable(user.getId()).compareTo(id) == 0){
                filterUser = user;
            }
        }
        return filterUser;
    }

    /**
     * Performs an in-order traversal of the AVL Tree and collects User data into a list.
     * @return List of User objects extracted from the tree nodes.
     */
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        inOrderTraversal(root, users);
        return users;
    }
    /**
     * Recursive helper method to perform in-order traversal and collect users.
     * @param node the current node in the traversal.
     * @param users the list of users being collected.
     */
    private void inOrderTraversal(AVLNode<T> node, List<User> users){
        if(node!=null){
            inOrderTraversal(node.left, users); //Visit the left subtree

            NodeData userData = node.getData();
            String id = (String) userData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_USER_ID.getAttributeName());
            String firstName = (String) userData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_FIRST_NAME.getAttributeName());
            String middleName = (String) userData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_MIDDLE_NAME.getAttributeName());
            String lastName = (String) userData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_LAST_NAME.getAttributeName());
            String dob = (String) userData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_DOB.getAttributeName());
            users.add(new User(id, firstName, middleName, lastName, dob));

            inOrderTraversal(node.right, users); //Visit the right subtree
        }
    }

    /**
     *
     * @return The root node of the AVL tree, which may be null if the tree is empty.
     */
    public AVLNode<T> getRoot() {
        return root;
    }

    /**
     * Checks if the entire AVL tree is balanced according to AVL tree rules.
     * An AVL tree is considered balanced if, for every node, the height difference
     * between its left and right subtree is at most 1.
     *
     * @return true if the tree is balanced, false otherwise.
     */
    public boolean isAVLTree() {
        return isAVLTreeValid(root);
    }

    /**
     * Recursively checks if a subtree rooted at a given node is balanced according to AVL tree rules.
     * This is a helper method used to determine whether the tree maintains the AVL property that
     * the height difference between the left and right subtree of any node is no more than 1.
     *
     * @param node The root node of the subtree to check for balance.
     * @return true if the subtree is balanced, false otherwise.
     */
    private boolean isAVLTreeValid(AVLNode<T> node) {
        if (node == null) return true;

        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        if (Math.abs(leftHeight - rightHeight) > 1) {
            return false;
        }
        return isAVLTreeValid(node.left) && isAVLTreeValid(node.right);
    }
}
