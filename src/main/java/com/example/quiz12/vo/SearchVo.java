package com.example.quiz12.vo;

import com.example.quiz12.entity.Question;

import java.time.LocalDate;
import java.util.List;

public class SearchVo extends CreateReq{
    private int quizId;

    public SearchVo() {

    }

    public SearchVo(int quizId,String name, String description, LocalDate startDate, LocalDate endDate, boolean published, List<Question> questionList) {
        super(name, description, startDate, endDate, published, questionList);
        this.quizId = quizId;
    }

    public int getQuizId() {
        return quizId;
    }
}
