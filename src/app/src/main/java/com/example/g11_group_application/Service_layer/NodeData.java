package com.example.g11_group_application.Service_layer;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: [Date of Creation, e.g., 12-April-2024]
 * Comments: This class is used to manage and encapsulate the data for nodes in a structured way.
 * It provides functionalities to add, retrieve, and convert attributes in a hashmap to JSON format.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class NodeData {
    private Map<String, Object> attributes; //Hashmap with String Column name Object Row value

    public NodeData() {
        this.attributes = new HashMap<>();
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    // Retrieves the value associated with a specific key in the attribute map.
    public Object getAttributeValue(String key) {
        return attributes.get(key);
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 20-April-2024
     * Converts attribute data into a JSONObject.
     * @return JSONObject representing all key-value pairs in the map
     */
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        // Iterating through the entry set of the hashmap
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json;
    }

    /**
     * @Author: Saksham Gupta (u7726995)
     * Created: 20-April-2024
     * Creates a NodeData instance from a JSONObject, populating it with data.
     * @param jsonObject The JSONObject from which to extract data.
     * @return A new NodeData instance populated with the extracted data.
     */
    public static NodeData fromJson(JSONObject jsonObject) throws JSONException {
        NodeData nodeData = new NodeData();
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            nodeData.addAttribute(key, jsonObject.get(key)); // Add each JSON entry to the map
        }
        return nodeData;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}

//Example of how to insert and serialize the data:

//        AVLTree tree = new AVLTree();
//        // Create NodeData objects for each insert operation
//        tree.insert(1, tree.createNodeData(FirestoreSchema.FireStoreCollection.ANCW_SECURITY_QUESTION_MASTER, 1,"What is your favorite color?"));
//        tree.insert(2, tree.createNodeData(FirestoreSchema.FireStoreCollection.ANCW_SECURITY_QUESTION_MASTER,2, "What city were you born in?"));
//        tree.insert(3, tree.createNodeData(FirestoreSchema.FireStoreCollection.ANCW_SECURITY_QUESTION_MASTER,3, "What is the name of your first pet?"));
//        tree.insert(4, tree.createNodeData(FirestoreSchema.FireStoreCollection.ANCW_SECURITY_QUESTION_MASTER,4, "Where did you study in highschool?"));
//        tree.insert(5, tree.createNodeData(FirestoreSchema.FireStoreCollection.ANCW_SECURITY_QUESTION_MASTER,5, "What is your mother's maiden name?"));
//
//        // Display the tree
//        System.out.println(tree.display());
//
//        // Serialize the AVL Tree to JSON
//        JSONObject serializedTree = tree.serializeToJson();
//        System.out.println("Serialized Tree:");
//        System.out.println(serializedTree.toString(4));  // Print JSON with indentation for readability
//
//        // Deserialize from JSON
//        AVLTree newTree = new AVLTree<>();
//        newTree.deserializeFromJson(serializedTree);
//        System.out.println("Tree after deserialization:");
//        System.out.println(newTree.display());
//
//        int searchId = 2;
//        //Updating attribute value
//        if(newTree.find(searchId)) {
//            NodeData data = newTree.search(searchId).getData();
//            data.getAttributes().replace("asqm_sec_question","What country were you born in?");
//            newTree.updateNodeData(searchId,data);
//            System.out.println(newTree.display());
//        }
//        else
//            System.out.println("Node " + searchId + " is not found!");
//
//        // Serialize the AVL Tree to JSON
//        serializedTree = newTree.serializeToJson();
//        System.out.println("Serialized New Tree:");
//        System.out.println(serializedTree.toString(4));  // Print JSON with indentation for readability