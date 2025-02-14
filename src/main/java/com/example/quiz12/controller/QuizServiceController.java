package com.example.quiz12.controller;

import com.example.quiz12.constants.ResMessage;
import com.example.quiz12.dao.FeedbackDao;
import com.example.quiz12.entity.Question;
import com.example.quiz12.service.ifs.FeedbackService;
import com.example.quiz12.service.ifs.QuizService;
import com.example.quiz12.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
public class QuizServiceController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping(value = "quiz/create")
    public BasicRes create(@RequestBody CreateReq req) {
        return quizService.create(req);
    }

    @PostMapping(value = "quiz/createList")
    public BasicRes create(@RequestBody List<CreateReq> reqList) {
        return quizService.createList(reqList);
    }

    @GetMapping(value = "quiz/get_all_quiz")
    public QuizSearchRes getAllQuiz() {
        return quizService.getAllQuiz();
    }

    @PostMapping(value = "quiz/get_quiz")
    public QuizSearchRes getQuiz(@RequestBody SearchReq req) {
        return quizService.getQuiz(req);
    }

    // 編輯用
    // 呼叫API的路徑: http://localhost:8080/quiz/get_ques_by_quiz_id?quizId=1(指定值)
    // quizId 名稱要和方法中的變數名稱一樣
    @GetMapping(value = "quiz/get_ques_by_quiz_id")
    public GetQuestionRes getQuesById(@RequestParam int quizId) {
        return quizService.getQuesById(quizId);
    }

    // 跟上面一樣，只是透過 @RequestParam 來指定路徑中的名稱
    // 呼叫API的路徑: c(指定值)
    // @RequestParam 中的 value，用來指定並對應路經?後面的名稱，並將路徑等號後面的值塞到方法的變數名稱中
    @PostMapping(value = "quiz/get_ques_list")
    public GetQuestionRes getQuesListByQuizId(@RequestParam(value = "quiz_id") int quizId) {
        return quizService.getQuesById(quizId);
    }

    //===========================================================================================
    // 多個參數使用 @RequestParam
    // 呼叫API的路徑: http://localhost:8080/quiz/search?name=AAA&start_date=2024-12-01
    @GetMapping(value = "quiz/search")
    public QuizSearchRes search( //
                                 @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                 @RequestParam(value = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam(value = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return null;
    }

    //===================================================
    @PostMapping(value = "quiz/update")
    public BasicRes update(@RequestBody UpdateReq req) {
        return quizService.update(req);
    }

    @PostMapping(value = "quiz/delete")
    public BasicRes delete(@RequestBody DeleteReq req) {
        return quizService.delete(req);
    }

    @PostMapping(value = "quiz/fillin")
    public BasicRes fillin(@RequestBody FillinReq req) {
        return feedbackService.fillin(req);
    }

    // ===================================================
    // 呼叫API的路徑: http://localhost:8080/quiz/statistics?quizId=1(指定值)
    @PostMapping(value = "quiz/statistics")
    public StatisticsRes statistics(@RequestParam(value = "quiz_id") int quizId) {
        return feedbackService.statistics(quizId);
    }
}

