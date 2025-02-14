package com.example.quiz12.vo;

import com.example.quiz12.entity.Question;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class CreateReq {
    // id 不用寫
    private String name;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private boolean published;

    public CreateReq() {
    }

    public CreateReq(String name, String description, LocalDate startDate, LocalDate endDate, boolean published, List<Question> questionList) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = published;
        this.questionList = questionList;
    }

    // 用Question包起來讓問題都放在一起
    private List<Question> questionList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublish(boolean published) {
        this.published = published;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}
