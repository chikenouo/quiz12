package com.example.quiz12.service.impl;

import com.example.quiz12.constants.QuesType;
import com.example.quiz12.constants.ResMessage;
import com.example.quiz12.dao.QuestionDao;
import com.example.quiz12.dao.QuizDao;
import com.example.quiz12.entity.Question;
import com.example.quiz12.entity.Quiz;
import com.example.quiz12.service.ifs.QuizService;
import com.example.quiz12.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {
    @Autowired
    private QuizDao quizDao;

    @Autowired
    private QuestionDao questionDao;

    // 因為同時新增問卷和問題(一個方法用了兩個DAO)
    // 新增多筆資料都算是同一筆的行為，所以要馬全部成功或全部失敗，所以使用 transactional 來保證一致性

    @Transactional(rollbackFor = Exception.class)
    @Override
    // rollbackOn = Exception.class: 指定@Transactional 資料回朔有效的例外層級
    // 發生例外(Exception)是 RuntimeException 或其子類別時，@Transactional 才會讓資料回朔，
    // 藉由 rollbackOn 可以指定發生哪個例外時，就可以讓資料回朔
    public BasicRes create(CreateReq req) {
        try {
            // 檢查參數
            BasicRes checkRes = checkParams(req);
            if (checkRes != null) {
                return checkRes;
            }
            // 因為是新增問卷，所以問卷的ID一定是0，但req沒有ques_id所以不用檢查
            // 因為 quiz 的 PK 是流水號，不會重複寫入，所以不用檢查資料料庫是否已存在相同的 PK

            // 新增問卷:
            // 因為 Quiz 中的 id 是 AI 自動生成的流水號，要讓 quizDao 執行 save 後可以把該 id 的值回傳，
            // 必須要在 Quiz 此 Entity 中將資料型態為 int 的屬性 id
            // 加上 @GeneratedValue(strategy = GenerationType.IDENTITY)
            // JPA 的 save，PK 已存在於 DB，會執行 update，若PK不存在，則會執行 insert

            // TODO 增加新增資料失敗: 使用try-catch 來處理，包含Transaction 資料的回溯層級

            Quiz quiz = quizDao.save(new Quiz(req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(), req.isPublished()));
            // 把quiz.id 的流水號抓下來塞到question 裡的quizId
            for (Question item : req.getQuestionList()) {
                item.setQuizId(quiz.getId());
            }
            // 新增問題:接下來question list 寫進Database
            // 如果想取最新的一筆，可以倒序再limit 1 或是 select max(quiz id) 不過這樣就只有一個id值
            questionDao.saveAll(req.getQuestionList()); // 如果不寫jpa 的save ，要先insert 再 select
            return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());

        } catch (Exception e) {
            return new BasicRes(ResMessage.DATA_SAVE_ERROR.getCode(), ResMessage.DATA_SAVE_ERROR.getMessage());
        }
    }

    @Override
    public BasicRes createList(List<CreateReq> reqList) {
        for(CreateReq item:reqList){
            BasicRes res = create(item);
            if(res.getCode()!= 200){
                return res;
            }
        }
        return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
    }

    @Override
    public QuizSearchRes getAllQuiz() {
        List<Quiz> res = quizDao.getAllQuiz();
        return new QuizSearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), res);
    }

    @Override
    public QuizSearchRes getQuiz(SearchReq req) {
        // 若Name沒有條件，前端帶過的資料可能是空字串或是Null
        // 要改變條件值:如果是NULL 或是空字串 或是 空白字串，一律改成空字串
        // 因為用% Like的時候是撈全部資料
        String name = req.getName();
        if (!StringUtils.hasText(name)) {
            // SQL語法中，欄位like %% (兩個%中間是空字串) 表示會忽略該欄位的條件值
            name = "";
        }

        // 設一個很大的range確保抓的到全部的資料
        LocalDate startDate = req.getStartDate();
        if (startDate == null) { //startDate == null 表示開始時間此欄位前端沒有帶值
            // 沒有帶值，可以直接指定時間到一個很早的時間點
            startDate = LocalDate.of(1970, 1, 1);
        }
        LocalDate endDate = req.getEndDate();
        if (endDate == null) {
            endDate = LocalDate.of(2999, 12, 31);
        }
        List<Quiz> res = quizDao.getQuiz(name, startDate, endDate);
        return new QuizSearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), res);
    }

    @Override
    public GetQuestionRes getQuesById(int quizId) {
        if (quizId <= 0) {
            return new GetQuestionRes(ResMessage.PARAM_QUIZ_ID_ERROR.getCode(), ResMessage.PARAM_QUIZ_ID_ERROR.getMessage());
        }
        List<Question> res = questionDao.getByQuizId(quizId);
        return new GetQuestionRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), res);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public BasicRes delete(DeleteReq req) {
        try {
            quizDao.deleteByQuizIdIn(req.getQuizIdList());
            questionDao.deleteByQuizIdIn(req.getQuizIdList());
        } catch (Exception e) {
            new BasicRes(ResMessage.DATA_SAVE_ERROR.getCode(), ResMessage.DATA_SAVE_ERROR.getMessage());
        }
        return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
    }

    // 更新
    @Transactional(rollbackFor = Exception.class)
    @Override
    public BasicRes update(UpdateReq req) {
        // 檢查參數
        BasicRes checkRes = checkParams(req);
        if (checkRes != null) {
            return checkRes;
        }
        // 檢查quizId是否存在
        int count = quizDao.selectCount(req.getQuizId());
        if (count != 1) {
            return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
        }
        // 檢查問題中的quizId 是否和req 中的 quizId 相同
        for (Question item : req.getQuestionList()) {
            if (req.getQuizId() != item.getQuizId()) {
                return new BasicRes(ResMessage.QUIZ_ID_MISMATCH.getCode(), ResMessage.QUIZ_ID_MISMATCH.getMessage());
            }
        }
        // 1. 更新quiz
        quizDao.updateById(req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(), req.isPublished(), req.getQuizId());

        try {
            quizDao.updateById(req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(), req.isPublished(), req.getQuizId());
            // 2. 刪除questions by quizId
            questionDao.deleteByQuizIdIn(List.of(req.getQuizId()));

            // 3.新增 questions
            questionDao.saveAll(req.getQuestionList());

        } catch (Exception e) {
            new BasicRes(ResMessage.DATA_UPDATE_ERROR.getCode(), ResMessage.DATA_UPDATE_ERROR.getMessage());
        }
        return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
    }


    // ==========================================================================================================
    // 檢查方法集合
    private BasicRes checkParams(CreateReq req) {
        // 使用排除法一一檢查
        if (!StringUtils.hasText(req.getName())) {
            return //
                    new BasicRes(ResMessage.PARAM_QUIZ_NAME_ERROR.getCode(), ResMessage.PARAM_QUIZ_NAME_ERROR.getMessage());
        }
        if (!StringUtils.hasText(req.getDescription())) {
            return //
                    new BasicRes(ResMessage.PARAM_DESCRIPTION_ERROR.getCode(), ResMessage.PARAM_DESCRIPTION_ERROR.getMessage());
        }
        if (req.getStartDate() == null) {
            return //
                    new BasicRes(ResMessage.PARAM_START_DATE_ERROR.getCode(), ResMessage.PARAM_START_DATE_ERROR.getMessage());
        }
        if (req.getEndDate() == null) {
            return //
                    new BasicRes(ResMessage.PARAM_END_DATE_ERROR.getCode(), ResMessage.PARAM_END_DATE_ERROR.getMessage());
        }
        // 結束時間不能早於開始時間
        if (req.getStartDate().isAfter(req.getEndDate())) {
            return //
                    new BasicRes(ResMessage.PARAM_DATE_ERROR.getCode(), ResMessage.PARAM_DATE_ERROR.getMessage());

        }

        //====== 檢查問題內容
        List<Question> quesList = req.getQuestionList();
        if (quesList.size() <= 0) {
            return new BasicRes(//
                    ResMessage.PARAM_QUES_LIST_ERROR.getCode(), ResMessage.PARAM_QUES_LIST_ERROR.getMessage());
        }
        for (Question item : quesList) {
            // 問題編號一定是從1開始，但無法檢查是否有按照順序以及中間是否有空缺的編號
            if (item.getQuesId() <= 0) {
                return new BasicRes(//
                        ResMessage.PARAM_QUES_ID_ERROR.getCode(), ResMessage.PARAM_QUES_ID_ERROR.getMessage());
            }
            if (!StringUtils.hasText(item.getQuesName())) {
                return new BasicRes(//
                        ResMessage.PARAM_QUES_NAME_ERROR.getCode(), ResMessage.PARAM_QUES_NAME_ERROR.getMessage());
            }
            if (!StringUtils.hasText(item.getType())) {
                return new BasicRes(//
                        ResMessage.PARAM_TYPE_ERROR.getCode(), ResMessage.PARAM_TYPE_ERROR.getMessage());
            }
            // TODO
            //檢查 1. type 是否是 單選、多選、文字
            if (!QuesType.checkType(item.getType())) {
                return new BasicRes(//
                        ResMessage.QUES_TYPE_MISMATCH.getCode(), ResMessage.QUES_TYPE_MISMATCH.getMessage());
            }
            //  檢查2. 文字類型時，options 不能有值
            if (item.getType().equalsIgnoreCase(QuesType.TEXT.getType()) && StringUtils.hasText(item.getOptions())) {
                return new BasicRes(//
                        ResMessage.PARAM_OPTIONS_ERROR.getCode(), ResMessage.PARAM_OPTIONS_ERROR.getMessage());
            }
        }
        return null;
    }
}
