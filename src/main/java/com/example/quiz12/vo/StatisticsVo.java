package com.example.quiz12.vo;

import java.util.List;

// 一個StatisticsVo 表示一個問題的答案次數
public class StatisticsVo {
    private String quizName;

    private int quesId;

    private String quesName;

    private boolean required;

    private String answer;

    List<OptionCount> optionCountList;

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public List<OptionCount> getOptionCountList() {
        return optionCountList;
    }

    public void setOptionCountList(List<OptionCount> optionCountList) {
        this.optionCountList = optionCountList;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
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
}
