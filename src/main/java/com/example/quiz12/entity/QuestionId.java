package com.example.quiz12.entity;

import java.io.Serializable;

// 實作序列化，才能傳輸給資料庫
// 其他class是因為Entity了，所以沒有實作Serializable
public class QuestionId implements Serializable {
    private int quizId;
    private int quesId;
    public QuestionId() {
    }

    public QuestionId(int quizId, int quesId) {
        this.quizId = quizId;
        this.quesId = quesId;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getQuesId() {
        return quesId;
    }

    public void setQuesId(int quesId) {
        this.quesId = quesId;
    }
}
