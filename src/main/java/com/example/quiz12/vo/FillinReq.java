package com.example.quiz12.vo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class FillinReq {

    private int quizId;

    private String userName;

    private String email;

    private int age;

    // 如果寫String，遇到複選題就要切割字串，這樣很麻煩
    // key = quesId, value = answers
    private Map<Integer, List<String>> quesIdAnswerMap;

    private LocalDate fillinDate = LocalDate.now(); // 當值是Null時，預設為當前日期，這樣就可免檢查

    public int getQuizId() {
        return quizId;
    }

    public void setQuiz_id(int quizId) {
        this.quizId = quizId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Map<Integer, List<String>> getQuesIdAnswerMap() {
        return quesIdAnswerMap;
    }

    public void setQuesIdAnswerMap(Map<Integer, List<String>> quesIdAnswerMap) {
        this.quesIdAnswerMap = quesIdAnswerMap;
    }

    public LocalDate getFillinDate() {
        return fillinDate;
    }

    public void setFillinDate(LocalDate fillinDate) {
        this.fillinDate = fillinDate;
    }
}
