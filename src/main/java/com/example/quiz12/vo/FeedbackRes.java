package com.example.quiz12.vo;

import com.example.quiz12.entity.Feedback;

import java.util.List;

public class FeedbackRes extends BasicRes{
    private List<FeedbackVo> feedbackVoList;

    public FeedbackRes() {

    }

    public FeedbackRes(int code, String message, List<FeedbackVo> feedbackVoList) {
        super(code, message);
        this.feedbackVoList = feedbackVoList;
    }

    public FeedbackRes(List<FeedbackVo> feedbackVoList) {
        this.feedbackVoList = feedbackVoList;
    }

    public FeedbackRes(int code, String message) {
    }

    public List<FeedbackVo> getFeedbackVoList() {
        return feedbackVoList;
    }
}
