package com.example.E_tech.Controller;

import com.example.E_tech.Service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discussions")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    // Top-level posts (paged)
    @GetMapping
    public ResponseEntity<Page<Map<String, Object>>> getDiscussions(
            @RequestParam String courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(discussionService.getDiscussions(courseId, page, size));
    }

    // Replies for a given post
    @GetMapping("/{id}/replies")
    public ResponseEntity<List<Map<String, Object>>> getReplies(@PathVariable String id) {
        return ResponseEntity.ok(discussionService.getReplies(id));
    }

    // Thread: post + replies
    @GetMapping("/thread/{id}")
    public ResponseEntity<Map<String, Object>> getThread(@PathVariable String id) {
        return ResponseEntity.ok(discussionService.getThread(id));
    }

    // Create a top-level post
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestHeader("email") String userEmail,
            @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(discussionService.mapDiscussionToDto(
                discussionService.createPost(
                        mapToDiscussion(payload), userEmail
                )
        ));
    }

    // Reply to a post
    @PostMapping("/{id}/reply")
    public ResponseEntity<Map<String, Object>> createReply(
            @RequestHeader("email") String userEmail,
            @PathVariable String id,
            @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(discussionService.mapDiscussionToDto(
                discussionService.createReply(id, mapToDiscussion(payload), userEmail)
        ));
    }

    // Update own post/reply
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(
            @RequestHeader("email") String userEmail,
            @PathVariable String id,
            @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(discussionService.mapDiscussionToDto(
                discussionService.updatePost(id, userEmail, mapToDiscussion(payload))
        ));
    }

    // Delete post/reply
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @RequestHeader("email") String userEmail,
            @PathVariable String id) {
        discussionService.deletePost(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    // Toggle like/unlike
    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @RequestHeader("email") String userEmail,
            @PathVariable String id) {
        return ResponseEntity.ok(discussionService.mapDiscussionToDto(
                discussionService.toggleLike(id, userEmail)
        ));
    }

    @GetMapping("/{courseId}/count-replies")
    public ResponseEntity<Long> countReplies(@PathVariable String courseId) {
        return ResponseEntity.ok(discussionService.countRepliesForCourse(courseId));
    }

    @GetMapping("/{courseId}/top-liked")
    public ResponseEntity<List<Map<String, Object>>> getTopLikedPosts(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(discussionService.getTopLikedPosts(courseId, limit)
                .stream().map(discussionService::mapDiscussionToDto).toList());
    }

    private static com.example.E_tech.Entity.Discussion mapToDiscussion(Map<String, Object> payload) {
        com.example.E_tech.Entity.Discussion d = new com.example.E_tech.Entity.Discussion();
        d.setContent((String) payload.get("content"));
        d.setCourseId((String) payload.get("courseId"));
        return d;
    }
}
