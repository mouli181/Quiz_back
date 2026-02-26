package com.example.quiz.repository;

import com.example.quiz.entity.QuizResult;
import com.example.quiz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    List<QuizResult> findByUser(User user);

    @Query("""
        SELECT u.username, MAX(q.percentage)
        FROM QuizResult q
        JOIN q.user u
        GROUP BY u.username
        ORDER BY MAX(q.percentage) DESC
    """)
    List<Object[]> getLeaderboard();


    @Query("""
        SELECT u.username, MAX(q.percentage)
        FROM QuizResult q
        JOIN q.user u
        WHERE q.topic = :topic
        GROUP BY u.username
        ORDER BY MAX(q.percentage) DESC
    """)
    List<Object[]> getLeaderboardByTopic(@Param("topic") String topic);
}
