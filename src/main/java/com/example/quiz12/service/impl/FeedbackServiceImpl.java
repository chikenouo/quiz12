package com.example.quiz12.service.impl;

import com.example.quiz12.constants.QuesType;
import com.example.quiz12.constants.ResMessage;
import com.example.quiz12.dao.FeedbackDao;
import com.example.quiz12.dao.QuestionDao;
import com.example.quiz12.dao.QuizDao;
import com.example.quiz12.entity.Feedback;
import com.example.quiz12.entity.Question;
import com.example.quiz12.entity.Quiz;
import com.example.quiz12.service.ifs.FeedbackService;
import com.example.quiz12.vo.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private QuizDao quizDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private FeedbackDao feedbackDao;

    @Override
    public BasicRes fillin(FillinReq req) {
        // 檢查參數
        BasicRes checkRes = checkParams(req);
        if (checkRes != null) {
            return checkRes;
        }
        // 2. 檢查問卷是否存在 + 3. 檢查問卷是否是發佈的
        if (quizDao.selectCountIsPublished(req.getQuizId()) != 1) {
            return new BasicRes(//
                    ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
        }
        // 4. 檢查同一email是否已經填寫過
        if (feedbackDao.selectCount(req.getQuizId(), req.getEmail()) != 0) {
            return new BasicRes(//
                    ResMessage.EMAIL_DUPLICATE.getCode(), ResMessage.EMAIL_DUPLICATE.getMessage());
        }
        // 利用quiz_id找出問卷(使用JPA方法):被Optional包起來主要用來提醒要判斷內容是否有值
        Optional<Quiz> op = quizDao.findById(req.getQuizId());
        // 判斷被Optionals包裝的Quiz是否有值
        if (op.isEmpty()) { //isEmpty() == true 時，表示從資料庫取回的Quiz 沒有資料
            return new BasicRes(//
                    ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
        }
        // 將Quiz 從 Optional 中取出
        Quiz quiz = op.get();
        // 4.1 檢查填寫的日期是否在問卷可填寫的範圍內
        LocalDate startDate = quiz.getStartDate();
        LocalDate endDate = quiz.getEndDate();
        LocalDate fillinDate = req.getFillinDate();
        // 判斷填寫時間是否在開始時間之前或結束時間之後
        // isBefore() and isAfter() 都不含當天
        if (fillinDate.isBefore(startDate) || fillinDate.isAfter(endDate)) {
            return new BasicRes(//
                    ResMessage.OUT_OF_FILLIN_DATE_RANGE.getCode(), ResMessage.OUT_OF_FILLIN_DATE_RANGE.getMessage());
        }
        // 4.2 比對相同題號中填寫的答案與選項是否配對(除了簡答之外)
        List<Question> quesList = questionDao.getByQuizId(req.getQuizId());
        //  map<題號, 答案(1~多個)>
        Map<Integer, List<String>> quesIdAnswerMap = req.getQuesIdAnswerMap();
        ObjectMapper mapper = new ObjectMapper();
        for (Question item : quesList) {
            // 比對題號
            int quesNumber = item.getQuesId();
            List<String> answerList = quesIdAnswerMap.get(quesNumber);
            // 排除:若該題為必填，但沒有答案
            if (item.isRequired() && CollectionUtils.isEmpty(answerList)) {
                return new BasicRes(//
                        ResMessage.ANSWER_IS_REQUIRED.getCode(), ResMessage.ANSWER_IS_REQUIRED.getMessage());
            }
            // 題目是單選或是簡答(文字)時:
            String quesType = item.getType();
            if (quesType.equalsIgnoreCase(QuesType.SINGLE.getType()) || quesType.equalsIgnoreCase(QuesType.TEXT.getType())) {
                // 答案不能有多個
                if (answerList.size() > 1) {
                    return new BasicRes(//
                            ResMessage.ONE_OPTION_IS_ALLOWED.getCode(), ResMessage.ONE_OPTION_IS_ALLOWED.getMessage());
                }
            }
            // 先排除題目類型是TEXT
            if (quesType.equalsIgnoreCase(QuesType.TEXT.getType())) {
                // 跳過當次
                continue;
            }
            // 將選項字串轉List<String>: 要先確定當初創建問卷時，前端的多個選項是陣列，且使用Stringify轉成字串型態
            // 前提: 前端的
            try {
                List<String> options = mapper.readValue(item.getOptions(), new TypeReference<>() {
                });
                // 比對相同題號中的選項與答案
                for (String answer : answerList) {
                    if (!options.contains(answer)) {
                        return new BasicRes(//
                                ResMessage.OPTION_ANSWER_MISMATCH.getCode(), ResMessage.OPTION_ANSWER_MISMATCH.getMessage());
                    }
                }
            } catch (Exception e) {
                return new BasicRes(//
                        ResMessage.OPTIONS_PARESER_ERROR.getCode(), ResMessage.OPTIONS_PARESER_ERROR.getMessage());
            }
        }
        // 存資料********
        List<Feedback> feedbackList = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> map : req.getQuesIdAnswerMap().entrySet()) {
            Feedback feedback = new Feedback();
            feedback.setQuizId(req.getQuizId());
            feedback.setUserName(req.getUserName());
            feedback.setEmail(req.getEmail());
            feedback.setQuesId(map.getKey());
            // 將List<String> 轉成字串
            try {
                String answerStr = mapper.writeValueAsString(map.getValue());
                feedback.setAnswer(answerStr);
            } catch (Exception e) {
                return new BasicRes(//
                        ResMessage.OPTIONS_PARESER_ERROR.getCode(), ResMessage.OPTIONS_PARESER_ERROR.getMessage());
            }
            feedback.setFillinDate(req.getFillinDate());
            feedbackList.add(feedback);
        }
        feedbackDao.saveAll(feedbackList);
        return new BasicRes(//
                ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
    }

    @Override
    public FeedbackRes feedback(int quizId) {
        if (quizId <= 0) {
            return new FeedbackRes(//
                    ResMessage.PARAM_QUES_ID_ERROR.getCode(), ResMessage.PARAM_QUES_ID_ERROR.getMessage());
        }
        List<FeedbackDto> feedbackList = feedbackDao.selectFeedbackByQuizId(quizId);
        // 整理資料
        List<FeedbackVo> feedbackVoList = new ArrayList<>();
        for (FeedbackDto item : feedbackList) {
            // 查看FeedbackVoList中是否有相同email
            FeedbackVo resVo = getEmail(feedbackVoList, item.getEmail());
            if (resVo != null) { // 表示FeedbackVoList中的FeedbackVo 已經存在相同email
                // 取出OptionAnswerList，此OPtionAnswerList已包含之前新增的OptionAnswer
                List<OptionAnswer> optionAnswerList = resVo.getOptionAnswerList();
                // 新增並設定同一張問卷不同問題以及答案
                OptionAnswer optionAnswer = new OptionAnswer();
                optionAnswer.setQuesId(item.getQuesId());
                optionAnswer.setQuesName(item.getQuesName());
                // 把答案字串轉成List<String>
                List<String> answerList = new ArrayList<>();
                try {
                    answerList = mapper.readValue(item.getAnswer(), new TypeReference<>() {
                    });
                } catch (Exception e) {
                    return new FeedbackRes(//
                            ResMessage.ANSWER_PARSE_ERROR.getCode(), ResMessage.ANSWER_PARSE_ERROR.getMessage());
                }
                optionAnswer.setAnswerList(answerList);
                optionAnswerList.add(optionAnswer);
                resVo.setOptionAnswerList(optionAnswerList);
//                feedbackVoList.add(resVo);
//                取出的feedbackVo 早已存在於feedbackVoList中，所以不需要再加入

            } else {    // 表示FeedbackVoList中的FeedbackVo 沒有相同email
                // 把資料塞進去
                FeedbackVo vo = new FeedbackVo();
                // 設定同一張問卷和同一位填寫者的資料
                vo.setQuizId(quizId);
                vo.setQuizName(item.getQuizName());
                vo.setDescription(item.getDescription());
                vo.setUserName(item.getUserName());
                vo.setEmail(item.getEmail());
                vo.setAge(item.getAge());
                vo.setFillinDate(item.getFillinDate());
                // 設定同一張問卷不同問題以及答案
                List<OptionAnswer> optionAnswerList = new ArrayList<>();
                OptionAnswer optionAnswer = new OptionAnswer();
                optionAnswer.setQuesId(item.getQuesId());
                optionAnswer.setQuesName(item.getQuesName());
                // 把答案字串轉成List<String>
                List<String> answerList = new ArrayList<>();
                try {
                    answerList = mapper.readValue(item.getAnswer(), new TypeReference<>() {
                    });
                } catch (Exception e) {
                    return new FeedbackRes(//
                            ResMessage.ANSWER_PARSE_ERROR.getCode(), ResMessage.ANSWER_PARSE_ERROR.getMessage());
                }
                optionAnswer.setAnswerList(answerList);
                optionAnswerList.add(optionAnswer);
                vo.setOptionAnswerList(optionAnswerList);

                feedbackVoList.add(vo);
            }
        }
        return new FeedbackRes(//
                ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), feedbackVoList);
    }

    // 統計邏輯處理
    @Override
    public StatisticsRes statistics(int quizId) {
        if (quizId <= 0) {
            return new StatisticsRes(//
                    ResMessage.PARAM_QUES_ID_ERROR.getCode(), ResMessage.PARAM_QUES_ID_ERROR.getMessage());
        }
        List<StatisticsDto> dtoList = feedbackDao.statistics(quizId);
        // 1. 集合每一題各自的所有答案:Map<題號, 答案(1~多個)>
        Map<Integer, List<String>> quesIdAnswerMap = gatherAnswer(dtoList);
        if (quesIdAnswerMap == null) {
            return new StatisticsRes(//
                    ResMessage.ANSWER_PARSE_ERROR.getCode(), ResMessage.ANSWER_PARSE_ERROR.getMessage());
        }
        // 2. 蒐集每一題的選項(不直接從答案次數計算，是考慮到可能會有極端情況:某個選項都沒人選)
        List<OptionCount> optionCountList = gatherOption(dtoList);
        if (optionCountList == null) {
            return new StatisticsRes(//
                    ResMessage.OPTIONS_PARESER_ERROR.getCode(), ResMessage.OPTIONS_PARESER_ERROR.getMessage());
        }
        // 3. 蒐集每一題每個選項的出現次數
        optionCountList = computeCount(quesIdAnswerMap, optionCountList);
        if (optionCountList == null) {
            return new StatisticsRes(// 不應該發生取不到答案
                    ResMessage.OPTIONS_COUNT_ERROR.getCode(), ResMessage.OPTIONS_COUNT_ERROR.getMessage());
        }
        // 4. 設定結果
        List<StatisticsVo> statisticsVoList = new ArrayList<>();
        for (StatisticsDto dto : dtoList) {
            StatisticsVo vo = new StatisticsVo();
            vo.setQuizName(dto.getQuizName());
            vo.setQuesId(dto.getQuesId());
            vo.setQuesName(dto.getQuesName());
            vo.setRequired(dto.isRequired());
            // 把相同題號的OptionCount 放一起
            List<OptionCount> ocList = new ArrayList<>();
            for (OptionCount oc : optionCountList) {
                if (oc.getQuesId() == dto.getQuesId()) {
                    // 相同題號的 畫，就把當初蒐集的OptionCount 放一起
                    ocList.add(oc);
                }
            }
            vo.setOptionCountList(ocList);
            statisticsVoList.add(vo);
        }
        return new StatisticsRes(// 不應該發生取不到答案
                ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), statisticsVoList);
    }

// ================================== 抽方法下來 =============================================

    // 把每一題的答案放到 quesIdAnswerMap 中
// 1. 若 quesIdAnswerMap 中已存在相同編號的 quesId  //
//     --> 從 quesIdAnswerMap 中取出相同 quesId 對應的答案 List<String>，//
//          並把轉化後的答案加再一起後並放回到 quesIdAnswerMap 中 //
// 2. 若 quesIdAnswerMap 中不存在相同編號的 quesId //
//      --> 把轉化後的答案 List<String> 放到 quesIdAnswerMap 中
    private Map<Integer, List<String>> gatherAnswer(List<StatisticsDto> dtoList) {
        Map<Integer, List<String>> quesIdAnswerMap = new HashMap<>();
        for (StatisticsDto item : dtoList) {
            //
            // 將Answer String 轉成 List<String>
            List<String> answerList = new ArrayList<>();
            try {
                answerList = mapper.readValue(item.getAnswer(), new TypeReference<>() {
                });
            } catch (Exception e) {
                return null;
            }
            // 若quesIdAnswerMap中已經存在相同編號的 List<String>，就從Map中取出
            if (quesIdAnswerMap.containsKey(item.getQuesId())) {
                List<String> answerListInmap = quesIdAnswerMap.get(item.getQuesId());
                // 把新的答案已經存在的answerList 加在一起
                answerList.addAll(answerListInmap);
                quesIdAnswerMap.put(item.getQuesId(), answerList);
            } else {
                quesIdAnswerMap.put(item.getQuesId(), answerList);
            }
        }
        return quesIdAnswerMap;
    }

    private List<OptionCount> gatherOption(List<StatisticsDto> dtoList) {
        List<OptionCount> optionCountList = new ArrayList<>();
        // 題號，是否已蒐集過選項
        Map<Integer, Boolean> map = new HashMap<>();
        for (StatisticsDto dto : dtoList) {
            // 跳過題型是TEXT的，因為沒有選項可以蒐集
            if (dto.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
                continue;
            }
            // map.get(dto.getQuesId())==true 的話，表示同一題的選項已經蒐集過了
            Boolean boo = map.get(dto.getQuesId());
            if (boo != null && boo == true) {
                continue;
            }
            // 轉換每一題的選項String 為 List<String>
            List<String> optionList = new ArrayList<>();
            try {
                optionList = mapper.readValue(dto.getOptions(), new TypeReference<>() {
                });
            } catch (Exception e) {
                return null;
            }
            // 蒐集題號跟選項
            for (String str : optionList) {
                // 相同題號下，每個不同˙的選項會有一個OptionList
                OptionCount oc = new OptionCount();
                oc.setQuesId(dto.getQuesId());
                oc.setOption(str);
                // 計算選項出現次數
                optionCountList.add(oc);
            }
            // 表示已蒐集過該題選項
            map.put(dto.getQuesId(), true);
        }
        return optionCountList;
    }


    // 此方法只計算有選項的題型(單、多選)
    private List<OptionCount> computeCount(Map<Integer, List<String>> quesIdAnswerMap, List<OptionCount> optionCountList) {
        // 因為是以選項為主，所以外層迴圈是optionCountList
        for (OptionCount item : optionCountList) {
            int quesId = item.getQuesId();
            String option = item.getOption();
            // 透過quesIdAnswerMap 找出對應的答案List<String>
            List<String> ansList = quesIdAnswerMap.get(quesId);
            if (ansList == null) {
                return null;
            }
            // 把List<String> 轉成單一字串，用來計算選項出現次數
            String ansStr = String.join("", ansList);
            // 計算選項出現次數
            int ansStrLength = ansStr.length(); // 原本串長度
            String newAnsStr = ansStr.replace(option, ""); // 把某個選項用空字串替換
            int newStrLength = newAnsStr.length(); // 扣掉某個選項後字串的長度
            // 判斷該選項都沒有人選
            if (ansStrLength == newStrLength) {
                item.setCount(0);
            } else {
                // 選項可能會有多個字，所以要計算次數應該是要除以選項的長度
                int count = (ansStrLength - newStrLength) / option.length();
                // 將次數設定回到OptionCount
                item.setCount(count);
            }
        }
        return optionCountList;
    }

    // 如果有存在我就抓原本已存在的email那筆
    private FeedbackVo getEmail(List<FeedbackVo> feedbackVoList, String targetEmail) {
        for (FeedbackVo vo : feedbackVoList) {
            if (vo.getEmail().equalsIgnoreCase(targetEmail)) {
                return vo;
            }
        }
        return null;
    }

    private BasicRes checkParams(FillinReq req) {
        if (req.getQuizId() <= 0) {
            return new BasicRes(//
                    ResMessage.PARAM_TYPE_ERROR.getCode(), ResMessage.PARAM_TYPE_ERROR.getMessage());
        }
        if (!StringUtils.hasText(req.getUserName())) {
            return new BasicRes(//
                    ResMessage.PARAM_USER_NAME_ERROR.getCode(), ResMessage.PARAM_USER_NAME_ERROR.getMessage());
        }
        if (!StringUtils.hasText(req.getEmail())) {
            return new BasicRes(//
                    ResMessage.PARAM_EMAIL_ERROR.getCode(), ResMessage.PARAM_EMAIL_ERROR.getMessage());
        }
        if (req.getAge() <= 0) {
            return new BasicRes(//
                    ResMessage.PARAM_AGE_ERROR.getCode(), ResMessage.PARAM_AGE_ERROR.getMessage());
        }
        // 因為FillinReq時間給預設值，前端速過來的REQ就算沒有值也會帶上預設值，所以不用檢查
        return null;
    }
}
