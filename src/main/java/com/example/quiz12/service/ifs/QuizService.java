package com.example.quiz12.service.ifs;

import com.example.quiz12.vo.*;

import java.util.List;

public interface QuizService {
    public BasicRes create(CreateReq req);

    public BasicRes createList(List<CreateReq> reqList);

    public QuizSearchRes getAllQuiz();

    public QuizSearchRes getQuiz(SearchReq req);

    public GetQuestionRes getQuesById(int quizId);

    public BasicRes delete(DeleteReq req);

    public BasicRes update(UpdateReq req);
}
