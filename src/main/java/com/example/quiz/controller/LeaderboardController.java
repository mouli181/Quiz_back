package com.example.quiz.controller;

import com.example.quiz.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaderboardController {

    private final QuizService quizService;

    public LeaderboardController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<?> leaderboard(
            @RequestParam(required = false) String topic) {

        if (topic != null && !topic.equalsIgnoreCase("All")) {
            return ResponseEntity.ok(quizService.getLeaderboardByTopic(topic));
        }

        return ResponseEntity.ok(quizService.getLeaderboard());
    }
}
