package com.example.E_tech.Service;

import com.example.E_tech.Entity.*;
import com.example.E_tech.Entity.Module;
import com.example.E_tech.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing user email"));
    }

    private Course getCourseByModuleId(String moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        return courseRepository.findById(module.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    private void validateTeacher(User user, Course course) {
        if (course.getTeacherId() == null || !course.getTeacherId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to modify quizzes for this course");
        }
    }

    public Quiz createQuiz(Quiz quiz, String email) {
        User user = getUserByEmail(email);
        Course course = getCourseByModuleId(quiz.getModuleId());
        validateTeacher(user, course);

        return quizRepository.save(quiz);
    }

    public Quiz updateQuiz(String id, Quiz quiz, String email) {
        User user = getUserByEmail(email);

        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        Course course = getCourseByModuleId(existingQuiz.getModuleId());
        validateTeacher(user, course);

        existingQuiz.setTitle(quiz.getTitle());
        existingQuiz.setQuestions(quiz.getQuestions());

        return quizRepository.save(existingQuiz);
    }

    public void deleteQuiz(String id, String email) {
        User user = getUserByEmail(email);

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        Course course = getCourseByModuleId(quiz.getModuleId());
        validateTeacher(user, course);

        quizRepository.delete(quiz);
    }

    public List<Quiz> getQuiz(String Moduleid, String email) {

        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        List< Enrollment> enrollments= enrollmentRepository.findByUserId(user.getId());
        Set<String> completedQuizIds = new HashSet<>();
        for (Enrollment enrollment : enrollments) {
            for (Enrollment.QuizResult result : enrollment.getQuizResults()) {
                completedQuizIds.add(result.getQuizId());
            }
        }
        List<Quiz> quizess=quizRepository.findByModuleId(Moduleid);
        return quizess.stream()
                .filter(quiz -> !completedQuizIds.contains(quiz.getId()))
                .collect(Collectors.toList());
    }
}
