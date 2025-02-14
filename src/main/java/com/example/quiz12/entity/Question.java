package com.example.quiz12.entity;


import jakarta.persistence.*;

@IdClass(value = QuestionId.class) // 把雙PK交給QuestionId管理
@Entity
@Table(name = "question")
public class Question {
    @Id
    @Column(name = "quiz_id")
    private int quizId;

    @Id
    @Column(name = "ques_id")
    private int quesId;

    @Column(name = "ques_name")
    private String quesName;

    @Column(name = "type")
    private String type;

    @Column(name = "required")
    private boolean required;

    @Column(name = "options")
    private String options;

    public Question() {
    }

    public Question(int quizId, int quesId, String quesName, String type, boolean required) {
        this.quizId = quizId;
        this.quesId = quesId;
        this.quesName = quesName;
        this.type = type;
        this.required = required;
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

    public String getQuesName() {
        return quesName;
    }

    public void setQuesName(String quesName) {
        this.quesName = quesName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
