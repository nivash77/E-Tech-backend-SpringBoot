package com.example.E_tech.Controller;

import com.example.E_tech.dto.ModuleRequest;
import com.example.E_tech.Entity.Module;
import com.example.E_tech.Service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @PostMapping("/add")
    public ResponseEntity<?> createModule(@RequestHeader("email") String email,
                                          @RequestBody ModuleRequest moduleRequest) {
        try {
            Module module = new Module();
            module.setCourseId(moduleRequest.getCourseId());
            module.setTitle(moduleRequest.getTitle());
            module.setOrder(moduleRequest.getOrder());

            Module createdModule = moduleService.createModule(module, email);
            return ResponseEntity.ok(createdModule);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateModule(@RequestHeader("email") String email,
                                          @PathVariable String id,
                                          @RequestBody ModuleRequest moduleRequest) {
        try {
            Module module = new Module();
            module.setTitle(moduleRequest.getTitle());
            module.setOrder(moduleRequest.getOrder());

            Module updatedModule = moduleService.updateModule(id, module, email);
            return ResponseEntity.ok(updatedModule);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteModule(@RequestHeader("email") String email,
                                          @PathVariable String id) {
        try {
            moduleService.deleteModule(id, email);
            return ResponseEntity.ok("Module deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Module>> getModulesByCourse(@PathVariable String courseId) {
        List<Module> modules = moduleService.getModulesByCourseId(courseId);
        return ResponseEntity.ok(modules);
    }
}
