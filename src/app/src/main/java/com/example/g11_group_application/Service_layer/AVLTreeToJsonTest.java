package com.example.g11_group_application.Service_layer;

import com.example.g11_group_application.firebase_connection_DAO.FirestoreSchema;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.json.JSONException;

public class AVLTreeToJsonTest {

    private AVLTree<Integer> avlTree;

    /**
     * Sets up the environment for each test case. This includes initializing the AVL tree
     * and populating it with some default values.
     */
    @Before
    public void setUp() {
        avlTree = new AVLTree<>();
        // Inserting elements in such a way that both rotations and balances are tested
        avlTree.insert(20, new NodeData());
        avlTree.insert(10, new NodeData());
        avlTree.insert(30, new NodeData());
        avlTree.insert(5, new NodeData());
        avlTree.insert(15, new NodeData());
        avlTree.insert(25, new NodeData());
        avlTree.insert(35, new NodeData());
    }

    /**
     * Tests the serialization of an AVL tree to a JSON object.
     */
    @Test
    public void testSerializeToJson() throws Exception {
        JSONObject json = avlTree.serializeToJson();
        assertNotNull("The JSON object should not be null after serialization", json);
        assertEquals("The JSON should contain all inserted nodes", 7, json.length());
    }

    /**
     * Tests the deserialization of an AVL tree from a JSON object.
     */
    @Test
    public void testDeserializeFromJson() throws Exception {
        JSONObject json = avlTree.serializeToJson();
        AVLTree newTree = new AVLTree<>();
        newTree.deserializeFromJson(json);

        assertTrue("Original tree should be a valid AVL tree", avlTree.isAVLTree());
        assertTrue("Deserialized tree should be a valid AVL tree", newTree.isAVLTree());
    }

    /**
     * Tests the deserialization from a malformed JSON string manually.
     */
    @Test
    public void testDeserializeFromMalformedJson() throws JSONException {
        JSONObject json = new JSONObject("{\"invalid\": \"data\"}");
        AVLTree<Integer> newTree = new AVLTree<>();

        try {
            newTree.deserializeFromJson(json); // This should throw JSONException
            fail("JSONException was expected due to malformed JSON");
        } catch (Exception e) {
            assertTrue("Exception should be JSONException", e instanceof JSONException);
        }
    }

    /**
     * Tests that no nodes are lost during the serialization and subsequent deserialization process.
     */
    @Test
    public void testIntegrityThroughSerializeDeserialize() throws Exception {
        JSONObject json = avlTree.serializeToJson();
        AVLTree<Integer> newTree = new AVLTree<>();
        newTree.deserializeFromJson(json);

        // Ensuring that all original tree nodes exist in the new tree
        assertTrue("All nodes should exist after deserialization",
                newTree.find(20) && newTree.find(10) && newTree.find(30) && newTree.find(5) &&
                        newTree.find(15) && newTree.find(25) && newTree.find(35));
    }

















    /*public static void main(String[] args) throws JSONException {
        AVLTree tree = new AVLTree();

        // Create NodeData objects for each insert operation
        tree.insert(1, tree.createNodeData(FirestoreSchema.FireStoreCollection.ANCW_SECURITY_QUESTION_MASTER, 1,"What is your favorite color?"));
        tree.insert(2, tree.createNodeData(FirestoreSchema.FireStoreCollection.ANCW_SECURITY_QUESTION_MASTER,2, "What city were you born in?"));
        tree.insert(3, tree.createNodeData(FirestoreSchema.FireStoreCollection.ANCW_SECURITY_QUESTION_MASTER,3, "What is the name of your first pet?"));
        tree.insert(4, tree.createNodeData(FirestoreSchema.FireStoreCollection.ANCW_SECURITY_QUESTION_MASTER,4, "Where did you study in highschool?"));
        tree.insert(5, tree.createNodeData(FirestoreSchema.FireStoreCollection.ANCW_SECURITY_QUESTION_MASTER,5, "What is your mother's maiden name?"));

        // Display the tree
        System.out.println(tree.display());

        // Serialize the AVL Tree to JSON
        JSONObject serializedTree = tree.serializeToJson();
        System.out.println("Serialized Tree:");
        System.out.println(serializedTree.toString(4));  // Print JSON with indentation for readability

        // Deserialize from JSON
        AVLTree newTree = new AVLTree<>();
        newTree.deserializeFromJson(serializedTree);
        System.out.println("Tree after deserialization:");
        System.out.println(newTree.display());

        int searchId = 2;
        //Updating attribute value
        if(newTree.find(searchId)) {
            NodeData data = newTree.search(searchId).getData();
            data.getAttributes().replace("asqm_sec_question","What country were you born in?");
            newTree.updateNodeData(searchId,data);
            System.out.println(newTree.display());
        }
        else
            System.out.println("Node " + searchId + " is not found!");

        // Serialize the AVL Tree to JSON
        serializedTree = newTree.serializeToJson();
        System.out.println("Serialized New Tree:");
        System.out.println(serializedTree.toString(4));  // Print JSON with indentation for readability

        AVLTree tree2 = new AVLTree();
        // Example 1
        tree2.insert("Smith", tree.createNodeData(
                FirestoreSchema.FireStoreCollection.ANCW_LOGIN_USER_MASTER,
                false,                          // ALUM_ABOR_TOR_FLAG
                "456 Elm Street",               // ALUM_ADDRESS_1
                "Apt 101",                      // ALUM_ADDRESS_2
                "Los Angeles",                  // ALUM_ADDRESS_3
                "Smitty",                       // ALUM_CAPTION
                "smith@example.com",            // ALUM_EMAIL_ADDRESS
                "Steve",                        // ALUM_FIRST_NAME
                "Male",                         // ALUM_GENDER
                "Smith",                        // ALUM_LAST_NAME
                "1998-07-19",                   // ALUM_DOB
                "SSteve123",                    // ALUM_LOGIN_ID
                "inactive",                     // ALUM_LOGIN_STATUS
                "Stephen",                      // ALUM_MIDDLE_NAME
                "9876543210",                   // ALUM_MOBILE_NUMBER
                "newpass321",                   // ALUM_NEW_PASSWORD
                new String[]{"oldpass1", "oldpass2"}, // ALUM_OLD_PASSWORD
                false,                          // ALUM_PHOTO_ADDED_FLAG
                "user",                          // ALUM_ROLE
                "SSteve123"                    // ALUM_USER_ID
        ));

        // Example 2
        tree2.insert("Johnson", tree.createNodeData(
                FirestoreSchema.FireStoreCollection.ANCW_LOGIN_USER_MASTER,
                true,                           // ALUM_ABOR_TOR_FLAG
                "789 Pine Street",              // ALUM_ADDRESS_1
                "",                             // ALUM_ADDRESS_2
                "Chicago",                      // ALUM_ADDRESS_3
                "JJ",                           // ALUM_CAPTION
                "johnsonj@example.com",         // ALUM_EMAIL_ADDRESS
                "Jessica",                      // ALUM_FIRST_NAME
                "Female",                       // ALUM_GENDER
                "Johnson",                      // ALUM_LAST_NAME
                "1998-07-19",                   // ALUM_DOB
                "JessicaJ2023",                 // ALUM_LOGIN_ID
                "active",                       // ALUM_LOGIN_STATUS
                "",                             // ALUM_MIDDLE_NAME
                "1928374650",                   // ALUM_MOBILE_NUMBER
                "password2023",                 // ALUM_NEW_PASSWORD
                new String[]{"jessOldPass1"},   // ALUM_OLD_PASSWORD
                true,                           // ALUM_PHOTO_ADDED_FLAG
                "editor",                        // ALUM_ROLE
                "JessicaJ2023"                    // ALUM_USER_ID
        ));

        // Example 3
        tree2.insert("Lee", tree.createNodeData(
                FirestoreSchema.FireStoreCollection.ANCW_LOGIN_USER_MASTER,
                false,                          // ALUM_ABOR_TOR_FLAG
                "123 Maple Avenue",             // ALUM_ADDRESS_1
                "Suite 500",                    // ALUM_ADDRESS_2
                "San Francisco",                // ALUM_ADDRESS_3
                "Lenny",                        // ALUM_CAPTION
                "lee.l@example.com",            // ALUM_EMAIL_ADDRESS
                "Leonard",                      // ALUM_FIRST_NAME
                "Male",                         // ALUM_GENDER
                "Lee",                          // ALUM_LAST_NAME
                "1998-07-19",                   // ALUM_DOB
                "LeeLeonard",                   // ALUM_LOGIN_ID
                "active",                       // ALUM_LOGIN_STATUS
                "L",                            // ALUM_MIDDLE_NAME
                "5556667777",                   // ALUM_MOBILE_NUMBER
                "leePass99",                    // ALUM_NEW_PASSWORD
                new String[]{"leePass88", "leePass77"}, // ALUM_OLD_PASSWORD
                false,                          // ALUM_PHOTO_ADDED_FLAG
                "administrator",                 // ALUM_ROLE
                "LeeLeonard"                    // ALUM_USER_ID
        ));

        // Example 4
        tree2.insert("Williams", tree.createNodeData(
                FirestoreSchema.FireStoreCollection.ANCW_LOGIN_USER_MASTER,
                true,                           // ALUM_ABOR_TOR_FLAG
                "321 Birch Road",               // ALUM_ADDRESS_1
                "Top Floor",                    // ALUM_ADDRESS_2
                "Seattle",                      // ALUM_ADDRESS_3
                "Willie",                       // ALUM_CAPTION
                "williamsw@example.net",        // ALUM_EMAIL_ADDRESS
                "Willa",                        // ALUM_FIRST_NAME
                "Non-binary",                   // ALUM_GENDER
                "Williams",                     // ALUM_LAST_NAME
                "1998-07-19",                   // ALUM_DOB
                "WillaW2023",                   // ALUM_LOGIN_ID
                "active",                       // ALUM_LOGIN_STATUS
                "Jo",                           // ALUM_MIDDLE_NAME
                "6543210987",                   // ALUM_MOBILE_NUMBER
                "willaSecure2023",              // ALUM_NEW_PASSWORD
                new String[]{"willa2019", "willa2020", "willa2021"}, // ALUM_OLD_PASSWORD
                true,                           // ALUM_PHOTO_ADDED_FLAG
                "contributor",                   // ALUM_ROLE
                "WillaW2023"                    // ALUM_USER_ID
        ));

        // Example 5
        tree2.insert("Brown", tree.createNodeData(
                FirestoreSchema.FireStoreCollection.ANCW_LOGIN_USER_MASTER,
                false,                          // ALUM_ABOR_TOR_FLAG
                "987 Oak Street",               // ALUM_ADDRESS_1
                "Unit 2B",                      // ALUM_ADDRESS_2
                "Miami",                        // ALUM_ADDRESS_3
                "Bobbie",                       // ALUM_CAPTION
                "brownb@example.org",           // ALUM_EMAIL_ADDRESS
                "Bobby",                        // ALUM_FIRST_NAME
                "Male",                         // ALUM_GENDER
                "Brown",                        // ALUM_LAST_NAME
                "1998-07-19",                   // ALUM_DOB
                "BobbyB2023",                   // ALUM_LOGIN_ID
                "suspended",                    // ALUM_LOGIN_STATUS
                "Robert",                       // ALUM_MIDDLE_NAME
                "1230984567",                   // ALUM_MOBILE_NUMBER
                "Bobby2023!",                   // ALUM_NEW_PASSWORD
                new String[]{"Bobby2022", "Bobby2021"}, // ALUM_OLD_PASSWORD
                false,                          // ALUM_PHOTO_ADDED_FLAG
                "guest",                         // ALUM_ROLE
                "BobbyB2023"                    // ALUM_USER_ID
        ));

        // Example 6
        tree2.insert("Doe", tree.createNodeData(
                FirestoreSchema.FireStoreCollection.ANCW_LOGIN_USER_MASTER,
                false,                      // ALUM_ABOR_TOR_FLAG
                "123 Fake Street",          // ALUM_ADDRESS_1
                "Apt 4",                    // ALUM_ADDRESS_2
                "New York",                 // ALUM_ADDRESS_3
                "Johnny",                   // ALUM_CAPTION
                "john@example.com",         // ALUM_EMAIL_ADDRESS
                "John",                     // ALUM_FIRST_NAME
                "Male",                     // ALUM_GENDER
                "Doe",                      // ALUM_LAST_NAME
                "1998-07-19",               // ALUM_DOB
                "JD123",                    // ALUM_LOGIN_ID
                "active",                   // ALUM_LOGIN_STATUS
                "Johnathan",                // ALUM_MIDDLE_NAME
                "1234567890",               // ALUM_MOBILE_NUMBER
                "pass123",                  // ALUM_NEW_PASSWORD
                new String[]{"passOld1", "passOld2", "passOld3"}, // ALUM_OLD_PASSWORD
                true,                       // ALUM_PHOTO_ADDED_FLAG
                "admin",                     // ALUM_ROLE
                "JD123"                    // ALUM_USER_ID
        ));

        // Optionally, serialize to JSON and display the structure
        JSONObject jsonTree = tree2.serializeToJson();
        System.out.println("Serialized New Tree:");
        System.out.println(jsonTree.toString(4)); // Print the JSON representation

        //Deserialize from JSON
        AVLTree newTree2 = new AVLTree<>();
        newTree2.deserializeFromJson(jsonTree);
        System.out.println("Tree after deserialization:");
        System.out.println(newTree2.display());
    }*/
}
