package com.example.g11_group_application.firebase_connection_DAO;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 21-April-2024
 * Comments: This class defines the schema for Firestore collections used in the application.
 * It provides a centralized definition of all document fields within various collections,
 * which helps maintain consistency and ease of reference throughout the codebase.
 */
public class FirestoreSchema {

    /**
     * Enum defining the collection names in Firestore for easy access throughout the application.
     */
    public enum FireStoreCollection {
        ANCW_LOGIN_USER_MASTER,
        ANCW_ROLE_MASTER,
        ANCW_SECURITY_QUESTION_MASTER,
        ANCW_USER_SECURITY_QUESTION
    }

    /**
     * Enum defining attribute names for the LoginUserMaster collection in Firestore.
     */
    public enum LoginUserMaster {
        ALUM_ABOR_TOR_FLAG("alum_abor_tor_flag"),
        ALUM_ADDRESS_1("alum_address_1"),
        ALUM_ADDRESS_2("alum_address_2"),
        ALUM_ADDRESS_3("alum_address_3"),
        ALUM_CAPTION("alum_caption"),
        ALUM_EMAIL_ADDRESS("alum_email_address"),
        ALUM_FIRST_NAME("alum_first_name"),
        ALUM_GENDER("alum_gender"),
        ALUM_LAST_NAME("alum_last_name"),
        ALUM_DOB("alum_DOB"),
        ALUM_LOGIN_ID("alum_login_id"),
        ALUM_LOGIN_STATUS("alum_login_status"),
        ALUM_MIDDLE_NAME("alum_middle_name"),
        ALUM_MOBILE_NUMBER("alum_mobile_number"),
        ALUM_NEW_PASSWORD("alum_new_password"),
        ALUM_OLD_PASSWORD("alum_old_password"),
        ALUM_PHOTO_ADDED_FLAG("alum_photo_added_flag"),
        ALUM_ROLE("alum_role"),
        ALUM_USER_ID("alum_user_id");

        private final String attributeName;

        //Sets the attribute name for the enum constant
        LoginUserMaster(String attributeName) {
            this.attributeName = attributeName;
        }

        //Gets the attribute name
        public String getAttributeName() {
            return this.attributeName;
        }
    }

    /**
     * Enum defining attribute names for the RoleMaster collection in Firestore.
     */
    public enum RoleMaster {
        ARM_ROLE_ID("arm_role_id"),
        ARM_ROLE_NAME("arm_role_name"),
        ARM_ROLE_PRIVILEGE("arm_role_privilege");

        private final String attributeName;

        //Sets the attribute name for the enum constant
        RoleMaster(String attributeName) {
            this.attributeName = attributeName;
        }

        //Gets the attribute name
        public String getAttributeName() {
            return this.attributeName;
        }
    }

    /**
     * Enum defining attribute names for the SecurityQuestionMaster collection in Firestore.
     */
    public enum SecurityQuestionMaster {
        ASQM_SEC_QU_ID("asqm_sec_qu_id"),
        ASQM_SEC_QUESTION("asqm_sec_question");

        private final String attributeName;

        //Sets the attribute name
        SecurityQuestionMaster(String attributeName) {
            this.attributeName = attributeName;
        }

        //Gets the attribute name
        public String getAttributeName() {
            return this.attributeName;
        }
    }

    /**
     * Enum defining attribute names for the UserSecurityQuestion collection in Firestore.
     */
    public enum UserSecurityQuestion {
        AUSQ_SEC_1_ANS("ausq_sec_1_ans"),
        AUSQ_SEC_1_ID("ausq_sec_1_id"),
        AUSQ_SEC_2_ANS("ausq_sec_2_ans"),
        AUSQ_SEC_2_ID("ausq_sec_2_id"),
        AUSQ_SEC_3_ANS("ausq_sec_3_ans"),
        AUSQ_SEC_3_ID("ausq_sec_3_id"),
        AUSQ_USER_ID("ausq_user_id");

        private final String attributeName;

        //Sets the attribute name for the enum constant
        UserSecurityQuestion(String attributeName) {
            this.attributeName = attributeName;
        }

        //Gets the attribute name
        public String getAttributeName() {
            return this.attributeName;
        }
    }
}