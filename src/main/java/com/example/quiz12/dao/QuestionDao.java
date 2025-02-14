package com.example.quiz12.dao;

import com.example.quiz12.entity.Question;
import com.example.quiz12.entity.QuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuestionDao extends JpaRepository<Question, QuestionId> {

    @Query(value = "SELECT * FROM question WHERE quiz_id =?1", nativeQuery = true)
    public List<Question> getByQuizId(int quizId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM question WHERE quiz_id in (?1)", nativeQuery = true)
    public int deleteByQuizIdIn(List<Integer> quizIds);
}
