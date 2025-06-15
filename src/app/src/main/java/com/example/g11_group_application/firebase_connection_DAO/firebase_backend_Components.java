package com.example.g11_group_application.firebase_connection_DAO;
/**
 * @Author: Divyesh Srivastava (u7726856)
 * Created: 20-April-2024
 * Comments: This is the Enum class that stores the list of all collections
 * (databases) and documents (tables)
 */
public enum firebase_backend_Components {
    firebase_email("pcrl2195@vbpxa.rdb"),
    firebase_password("XpAqOhktaQHcws2R65sK/Q=="),
    firebase_3DES_Decryption_Key("HduilpgtRdchigjrixdc"),
    collection_name("ancw"),
    user_login_master("ancw_login_user_master"),
    role_master("ancw_role_master"),
    user_security_questions("ancw_user_security_question"),
    security_question_master("ancw_security_question_master");

    String component_name = "";
    firebase_backend_Components(String component_name){
        this.component_name = component_name;
    }

    public String getComponent_name(){
        return component_name;
    }
};
;
