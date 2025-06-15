package com.example.g11_group_application.firebase_connection_DAO;
/**
 * @Author: Divyesh Srivastava (u7726856)
 * Created: 22-April-2024
 * Comments: This is the Enum class that stores the list of primary keys in each document
 */
public enum firebase_primary_key_attributes {
    user_login_master("alum_user_id"),
    role_master("arm_role_id"),
    user_security_questions("ausq_user_id"),
    security_question_master("asqm_sec_qu_id");

    String component_name = "";

    firebase_primary_key_attributes(String component_name) {
        this.component_name = component_name;
    }

    public String getComponent_name() {
        return component_name;
    }
}
