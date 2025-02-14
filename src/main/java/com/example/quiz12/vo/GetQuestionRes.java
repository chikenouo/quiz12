package com.example.quiz12.vo;

import com.example.quiz12.entity.Question;

import java.util.List;

public class GetQuestionRes extends BasicRes {
    private List<Question> quesList;

    public GetQuestionRes() {
        super();
    }

    public GetQuestionRes(int code, String message) {
        super(code, message);
    }

    public GetQuestionRes(int code, String message, List<Question> quesList) {
        super(code, message);
        this.quesList = quesList;
    }

    public List<Question> getQuesList() {
        return quesList;
    }
}
