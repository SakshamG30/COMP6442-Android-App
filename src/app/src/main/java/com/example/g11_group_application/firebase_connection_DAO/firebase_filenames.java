package com.example.g11_group_application.firebase_connection_DAO;
/**
 * @Author: Divyesh Srivastava (u7726856)
 * Created: 20-April-2024
 * Comments: This is the Enum class that contains the names of all
 * the files that are to be downloaded or uploaded in or from the app
 */
public enum firebase_filenames {
    user_login_master_json("user_master_data.json"),
    user_login_master_csv("user_master_data.csv"),
    role_master_json("role_master.json"),
    user_security_questions_json("user_security_questions.json"),
    user_security_questions_csv("user_security_questions.csv"),
    security_question_master_json("security_questions.json"),
    security_question_master_csv("security_questions.csv"),
    datastream_data_csv("wellbeing_dataset.csv");

    String component_name = "";

    firebase_filenames(String component_name) {
        this.component_name = component_name;
    }

    public String getComponent_name() {
        return component_name;
    }
}
