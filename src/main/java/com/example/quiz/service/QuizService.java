package com.example.quiz.service;

import com.example.quiz.dto.QuizResultRequest;
import com.example.quiz.entity.QuizResult;
import com.example.quiz.entity.User;
import com.example.quiz.repository.QuizResultRepository;
import com.example.quiz.repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {

    private final QuizResultRepository quizResultRepository;
    private final UserRepository userRepository;

    public QuizService(QuizResultRepository quizResultRepository,
                       UserRepository userRepository) {
        this.quizResultRepository = quizResultRepository;
        this.userRepository = userRepository;
    }

    public void saveResult(QuizResultRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int percentage = (int) ((double) request.getScore() / request.getTotal() * 100);

        QuizResult result = new QuizResult();
        result.setScore(request.getScore());
        result.setTotal(request.getTotal());
        result.setPercentage(percentage);
        result.setCreatedAt(LocalDateTime.now());
        result.setTopic(request.getTopic());
        result.setUser(user);

        quizResultRepository.save(result);
    }

    public List<QuizResult> getHistory(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return quizResultRepository.findByUser(user);
    }

    public List<Object[]> getLeaderboard() {
        return quizResultRepository.getLeaderboard();
    }

    public List<Object[]> getLeaderboardByTopic(String topic) {
        return quizResultRepository.getLeaderboardByTopic(topic);
    }

}
