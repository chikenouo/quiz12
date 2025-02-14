package com.example.quiz12.vo;

import java.time.LocalDate;

// 用來將多張表資料撈回來後的裝載容器
public class FeedbackDto {
    private String quizName;

    private String description;

    private String userName;

    private String email;

    private int age;

    private LocalDate fillinDate;

    private int quesId;

    private String quesName;

    private String answer;

    public FeedbackDto(){

    }

    public FeedbackDto(String quizName, String description, String userName, String email, int age, String answer, LocalDate fillinDate, int quesId, String quesName) {
        this.quizName = quizName;
        this.description = description;
        this.userName = userName;
        this.email = email;
        this.age = age;
        this.answer = answer;
        this.fillinDate = fillinDate;
        this.quesId = quesId;
        this.quesName = quesName;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuesName() {
        return quesName;
    }

    public void setQuesName(String quesName) {
        this.quesName = quesName;
    }

    public int getQuesId() {
        return quesId;
    }

    public void setQuesId(int quesId) {
        this.quesId = quesId;
    }

    public LocalDate getFillinDate() {
        return fillinDate;
    }

    public void setFillinDate(LocalDate fillinDate) {
        this.fillinDate = fillinDate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
