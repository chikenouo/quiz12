package com.example.quiz12.vo;

public class StatisticsDto {
    private String quizName;
    private int quesId;
    private String quesName;
    private boolean required;
    private String options; // 答案要從這邊取，因為從回饋業可能會抓不到沒人選的選項，這樣統計不對
    private String type;
    private String answer;


    public StatisticsDto() {

    }

    public StatisticsDto(String quizName, int quesId, String quesName, boolean required, String options, String type,String answer) {
        this.quizName = quizName;
        this.quesId = quesId;
        this.quesName = quesName;
        this.required = required;
        this.options = options;
        this.type = type;
        this.answer = answer;
    }

    public String getQuizName() {
        return quizName;
    }

    public int getQuesId() {
        return quesId;
    }

    public String getQuesName() {
        return quesName;
    }

    public boolean isRequired() {
        return required;
    }

    public String getAnswer() {
        return answer;
    }

    public String getOptions() {
        return options;
    }

    public String getType() {
        return type;
    }
}
