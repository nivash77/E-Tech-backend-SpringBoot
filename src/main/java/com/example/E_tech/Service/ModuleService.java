package com.example.E_tech.Service;

import com.example.E_tech.Entity.Module;
import com.example.E_tech.Entity.Course;
import com.example.E_tech.Entity.User;
import com.example.E_tech.Repository.CourseRepository;
import com.example.E_tech.Repository.ModuleRepository;
import com.example.E_tech.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public Module createModule(Module module, String teacherEmail) {
        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Course course = courseRepository.findById(module.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacherId().equals(teacher.getId())) {
            throw new RuntimeException("Unauthorized: You cannot add modules to this course");
        }

        return moduleRepository.save(module);
    }

    public Module updateModule(String id, Module module, String teacherEmail) {
        Module existing = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Course course = courseRepository.findById(existing.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacherId().equals(teacher.getId())) {
            throw new RuntimeException("Unauthorized: You cannot update this module");
        }

        existing.setTitle(module.getTitle());
        existing.setOrder(module.getOrder());

        return moduleRepository.save(existing);
    }

    public void deleteModule(String id, String teacherEmail) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Course course = courseRepository.findById(module.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacherId().equals(teacher.getId())) {
            throw new RuntimeException("Unauthorized: You cannot delete this module");
        }

        moduleRepository.delete(module);
    }

    public List<Module> getModulesByCourseId(String courseId) {
        return moduleRepository.findByCourseIdOrderByOrderAsc(courseId);
    }
}
