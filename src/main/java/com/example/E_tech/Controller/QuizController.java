package com.example.E_tech.Controller;

import com.example.E_tech.Entity.Quiz;
import com.example.E_tech.Service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/add")
    public ResponseEntity<?> createQuiz(@RequestHeader("email") String email, @RequestBody Quiz quiz) {
        Quiz createdQuiz = quizService.createQuiz(quiz, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@RequestHeader("email") String email,
                                        @PathVariable String id,
                                        @RequestBody Quiz quiz) {
        Quiz updatedQuiz = quizService.updateQuiz(id, quiz, email);
        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@RequestHeader("email") String email, @PathVariable String id) {
        quizService.deleteQuiz(id, email);
        return ResponseEntity.ok("Quiz deleted successfully");
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getQUiz(@RequestHeader("email") String email,@PathVariable String id){
        List<Quiz>  quizzes=quizService.getQuiz(id,email);
        return ResponseEntity.ok(quizzes);
    }
}
