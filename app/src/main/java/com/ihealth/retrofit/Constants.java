package com.ihealth.retrofit;

/**
 * 常量值
 * @author liyanwen
 */
public class Constants {
    public static final String BACKEND_URL_PRODUCTION = "https://pigeon.ihealthlabs.com.cn";
    public static final String BACKEND_URL_STAGING = "https://testpigeon.ihealthlabs.com.cn";
    public static final String BACKEND_URL_LOCAL = "http://172.16.0.178:3080";
//    public static final String BACKEND_URL_LOCAL = "http://172.16.0.68:3080";

    public static final int FACE_RESPONSE_CODE_SUCCESS = 0;
    public static final int FACE_RESPONSE_CODE_ERROR_SEARCH_USER_NOT_FOUND = 1001;
    public static final int FACE_RESPONSE_CODE_ERROR_SEARCH_USER_FOUND_NOT_MATCH = 1002;
    public static final int FACE_RESPONSE_CODE_ERROR_SEARCH_OTHER_ERRORS = 1003;
    public static final int FACE_RESPONSE_CODE_ERROR_ADD_USER_USER_NOT_EXIST = 2001;
    public static final int FACE_RESPONSE_CODE_ERROR_ADD_USER_OTHER_ERRORS = 2002;
    public static final int FACE_RESPONSE_CODE_ERROR_DETECT_USER_FACE_INVALID = 3001;
    public static final int FACE_RESPONSE_CODE_ERROR_ALREADY_SIGNED_IN = 4001;
    public static final int FACE_RESPONSE_CODE_ERROR_NEED_CONTACT_CDE = 4002;
    public static final int FACE_RESPONSE_CODE_ERROR_SHOULD_CHECK_CERTAIN_DAY = 4003;
    public static final int FACE_RESPONSE_CODE_ERROR_OTHER_REASONS = 4004;

    public static final String SP_NAME_AUTHORIZATION="sp_name_authorization";
    public static final String SP_KEY_TOKEN="sp_key_token";

    public static final String SP_NAME_HOSPITAL_INFOS="sp_name_hospital_infos";
    public static final String SP_KEY_HOSPITAL_LOGO_URL="sp_key_hospital_logo_url";
    public static final String SP_KEY_HOSPITAL_FULL_NAME="sp_key_hospital_full_name";
    public static final String SP_KEY_HOSPITAL_GROUP_ID="sp_key_hospital_group_id";

    public static final String SP_NAME_PATIENT_INFOS="sp_name_patient_infos";
    public static final String SP_KEY_PATIENT_ID="sp_key_patient_id";
    public static final String SP_KEY_PATIENT_PRINTED="sp_key_patient_printed";

}
