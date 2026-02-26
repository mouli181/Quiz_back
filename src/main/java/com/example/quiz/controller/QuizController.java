package com.example.quiz.controller;

import com.example.quiz.dto.QuizResultRequest;
import com.example.quiz.entity.QuizResult;
import com.example.quiz.service.QuizService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // ✅ SAVE QUIZ RESULT
    @PostMapping("/save")
    public String saveResult(@RequestBody QuizResultRequest request,
                             Authentication authentication) {

        String email = authentication.getName(); // 🔥 GET LOGGED USER EMAIL

        quizService.saveResult(request, email);

        return "Quiz result saved successfully";
    }

    // ✅ GET HISTORY
    @GetMapping("/history")
    public List<QuizResult> getHistory(Authentication authentication) {

        String email = authentication.getName(); // 🔥 GET LOGGED USER EMAIL

        return quizService.getHistory(email);
    }

    // ✅ LEADERBOARD
    @GetMapping("/leaderboard")
    public List<Object[]> getLeaderboard() {
        return quizService.getLeaderboard();
    }
}
