package com.example.quiz12.constants;

public enum ResMessage {
    // 列舉項目
    SUCCESS(200,"Success!"),//
    PARAM_QUIZ_NAME_ERROR(400,"Param quiz name error!!"),//
    PARAM_DESCRIPTION_ERROR(400,"Param description error!!"),//
    PARAM_START_DATE_ERROR(400,"Param start date error!!"),//
    PARAM_END_DATE_ERROR(400,"Param end date error!!"),//
    PARAM_QUES_ID_ERROR(400,"Param question id error!!"),//
    PARAM_QUES_NAME_ERROR(400,"Param question name error!!"),//
    PARAM_EMAIL_ERROR(400,"Param email error!!"),//
    PARAM_AGE_ERROR(400,"Param age error!!"),//
    PARAM_TYPE_ERROR(400,"Param type error!!"),//
    PARAM_USER_NAME_ERROR(400,"Param user name error!!"),//
    PARAM_OPTIONS_ERROR(400,"Param options error!!"),//
    PARAM_QUES_LIST_ERROR(400,"Param question list error!!"),//
    PARAM_DATE_ERROR(400,"Param date error!!"),//
    DATA_SAVE_ERROR(500,"Data save error!!"),//
    DATA_UPDATE_ERROR(500,"Data update error!!"),//
    QUES_TYPE_MISMATCH(400,"Question type mismatch!!"),//
    PARAM_QUIZ_ID_ERROR(400,"Param quiz id error!!"),//
    QUIZ_NOT_FOUND(404,"Quiz not found!!"),//
    QUIZ_ID_MISMATCH(400,"Quiz id mismatch!!"),//
    EMAIL_DUPLICATE(400,"Email duplicate!!"),//
    OUT_OF_FILLIN_DATE_RANGE(400,"Out of fill-in date range!!"),//
    ANSWER_IS_REQUIRED(400,"Answer is required!!"),//
    ONE_OPTION_IS_ALLOWED(400,"One option is allowed!!"),//
    OPTIONS_PARESER_ERROR(400,"Options parser error!!"),//
    OPTION_ANSWER_MISMATCH(400,"Option answer mismatch!!"),//
    ANSWER_PARSE_ERROR(400,"Answer parse error!!"),//
    OPTIONS_COUNT_ERROR(400,"Options count error!!"),//
    ;

    private int code;

    private String message;


    private ResMessage(int statusCode, String message) {
        this.message = message;
        this.code = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
