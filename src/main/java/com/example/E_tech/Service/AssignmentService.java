package com.example.E_tech.Service;

import com.example.E_tech.Entity.*;
import com.example.E_tech.Entity.Module;
import com.example.E_tech.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    public Assignment createAssignment(Assignment assignment, String userEmail) {
        User teacher = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Module module=moduleRepository.findById(assignment.getModuleId()).orElseThrow(()-> new RuntimeException("Module not found"));
        Course course=courseRepository.findById(module.getCourseId()).orElseThrow(()->new RuntimeException("Course not found"));

        if(!course.getTeacherId().equals(teacher.getId())){
            throw new RuntimeException("This User not valid to Update assignments");
        }
        if (!"TEACHER".toLowerCase().equals(teacher.getRole().toLowerCase())) {
            throw new RuntimeException("Only teachers can create assignments ");
        }

        moduleRepository.findById(assignment.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));

        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(String id, Assignment updatedAssignment, String email) {
        Assignment existing = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Module module=moduleRepository.findById(existing.getModuleId()).orElseThrow(()-> new RuntimeException("Module not found"));
        Course course=courseRepository.findById(module.getCourseId()).orElseThrow(()->new RuntimeException("Course not found"));

        if(!course.getTeacherId().equals(teacher.getId())){
            throw new RuntimeException("This User not valid to Update assignments");
        }

        if (!"TEACHER".equals(teacher.getRole())) {
            throw new RuntimeException("Only teachers can update assignments");
        }

        moduleRepository.findById(existing.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));

        existing.setTitle(updatedAssignment.getTitle());
        existing.setDescription(updatedAssignment.getDescription());
        existing.setDeadline(updatedAssignment.getDeadline());
        return assignmentRepository.save(existing);
    }

    public void deleteAssignment(String id, String email) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Module module=moduleRepository.findById(assignment.getModuleId()).orElseThrow(()-> new RuntimeException("Module not found"));
        Course course=courseRepository.findById(module.getCourseId()).orElseThrow(()->new RuntimeException("Course not found"));

        if(!course.getTeacherId().equals(teacher.getId())){
            throw new RuntimeException("This User not valid to Update assignments");
        }
        if (!"TEACHER".equals(teacher.getRole())) {
            throw new RuntimeException("Only teachers can delete assignments");
        }

        assignmentRepository.delete(assignment);
    }

    public Assignment getAssignmentById(String id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
    }
    public List<Assignment> getUpcomingDeadlines(String email) {
        // 1. Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Get enrollments for the user
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(user.getId());
        if (enrollments.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. Get course IDs
        List<String> courseIds = enrollments.stream()
                .map(Enrollment::getCourseId)
                .toList();

        // 4. Get modules for those courses
        List<Module> modules = moduleRepository.findByCourseIdIn(courseIds);
        if (modules.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> moduleIds = modules.stream()
                .map(Module::getId)
                .toList();

        // 5. Get assignments for those modules
        List<Assignment> assignments = assignmentRepository.findByModuleIdIn(moduleIds);

        // 6. Filter future deadlines & sort by deadline
        return assignments.stream()
                .filter(a -> a.getDeadline() != null && a.getDeadline().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Assignment::getDeadline))
                .toList();
    }
    public List<Assignment> getPendingAssignments(String teacherEmail) {
        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"TEACHER".toLowerCase().equals(teacher.getRole().toLowerCase())) {
            throw new RuntimeException("Only teachers can view pending assignments");
        }

        // Get courses taught by this teacher
        List<String> courseIds = moduleRepository.findAll().stream()
                .filter(m -> {
                    try {
                        return courseRepository.findById(m.getCourseId())
                                .map(c -> c.getTeacherId().equals(teacher.getId()))
                                .orElse(false);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(Module::getId)
                .toList();

        // Fetch assignments in those courses that are pending grading
        return assignmentRepository.findByModuleIdIn(courseIds).stream()
                .filter(a -> a.getDeadline() != null) // optional: filter not expired
                .collect(Collectors.toList());
    }


    public List<Assignment> getAssignmentOfTeacher(String email) {
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        List<Assignment> assignments=assignmentRepository.findByTeacherId(user.getId());
        return assignments;
    }
}
