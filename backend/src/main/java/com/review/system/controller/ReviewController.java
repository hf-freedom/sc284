package com.review.system.controller;

import com.review.system.dto.AppealResultDTO;
import com.review.system.model.Content;
import com.review.system.model.Reviewer;
import com.review.system.model.User;
import com.review.system.service.ReviewQueueService;
import com.review.system.service.ReviewService;
import com.review.system.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewQueueService reviewQueueService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SchedulerService schedulerService;

    @PostMapping("/content/submit")
    public ResponseEntity<Content> submitContent(@RequestBody Content content) {
        Content result = reviewQueueService.submitContent(content);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/content/all")
    public ResponseEntity<List<Content>> getAllContents() {
        return ResponseEntity.ok(reviewQueueService.getAllContents());
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable String id) {
        Content content = reviewQueueService.getContentById(id);
        return content != null ? ResponseEntity.ok(content) : ResponseEntity.notFound().build();
    }

    @GetMapping("/content/pending")
    public ResponseEntity<List<Content>> getPendingContents() {
        return ResponseEntity.ok(reviewQueueService.getPendingContents());
    }

    @GetMapping("/review/next/{reviewerId}")
    public ResponseEntity<Content> getNextReviewTask(@PathVariable String reviewerId) {
        Content content = reviewQueueService.getNextReviewTask(reviewerId);
        return content != null ? ResponseEntity.ok(content) : ResponseEntity.notFound().build();
    }

    @GetMapping("/review/next/{reviewerId}/{type}")
    public ResponseEntity<Content> getNextReviewTaskByType(@PathVariable String reviewerId, @PathVariable String type) {
        Content content = reviewQueueService.getNextReviewTaskByType(reviewerId, type);
        return content != null ? ResponseEntity.ok(content) : ResponseEntity.notFound().build();
    }

    @PostMapping("/review/process")
    public ResponseEntity<Content> reviewContent(@RequestBody Map<String, String> request) {
        String contentId = request.get("contentId");
        String reviewerId = request.get("reviewerId");
        String result = request.get("result");
        String comment = request.get("comment");
        Content content = reviewService.reviewContent(contentId, reviewerId, result, comment);
        return content != null ? ResponseEntity.ok(content) : ResponseEntity.notFound().build();
    }

    @PostMapping("/content/appeal")
    public ResponseEntity<Content> appealContent(@RequestBody Map<String, String> request) {
        String contentId = request.get("contentId");
        String appealReason = request.get("appealReason");
        Content content = reviewService.appealContent(contentId, appealReason);
        return content != null ? ResponseEntity.ok(content) : ResponseEntity.notFound().build();
    }

    @GetMapping("/content/appealed")
    public ResponseEntity<List<Content>> getAppealedContents() {
        return ResponseEntity.ok(reviewService.getAppealedContents());
    }

    @PostMapping("/appeal/process")
    public ResponseEntity<AppealResultDTO> processAppeal(@RequestBody Map<String, Object> request) {
        String contentId = (String) request.get("contentId");
        boolean appealSuccess = (Boolean) request.get("appealSuccess");
        String comment = (String) request.get("comment");
        AppealResultDTO result = reviewService.processAppeal(contentId, appealSuccess, comment);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users/pending-ban")
    public ResponseEntity<List<User>> getPendingBanUsers() {
        return ResponseEntity.ok(reviewService.getPendingBanUsers());
    }

    @PostMapping("/ban/process")
    public ResponseEntity<User> processBanApproval(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        boolean approved = (Boolean) request.get("approved");
        String comment = (String) request.get("comment");
        User user = reviewService.processBanApproval(userId, approved, comment);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/reviewers")
    public ResponseEntity<List<Reviewer>> getAllReviewers() {
        return ResponseEntity.ok(reviewService.getAllReviewers());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(reviewService.getAllUsers());
    }

    @GetMapping("/stats/queues")
    public ResponseEntity<Map<String, Integer>> getQueueStats() {
        return ResponseEntity.ok(reviewQueueService.getQueueStats());
    }

    @GetMapping("/stats/summary")
    public ResponseEntity<Map<String, Object>> getSummaryStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Content> contents = reviewQueueService.getAllContents();
        
        int approved = 0, rejected = 0, inReview = 0, appealed = 0;
        for (Content c : contents) {
            switch (c.getStatus()) {
                case "APPROVED": approved++; break;
                case "REJECTED": rejected++; break;
                case "IN_REVIEW": inReview++; break;
                case "APPEALED": appealed++; break;
            }
        }
        
        stats.put("total", contents.size());
        stats.put("approved", approved);
        stats.put("rejected", rejected);
        stats.put("inReview", inReview);
        stats.put("appealed", appealed);
        stats.put("reviewers", reviewService.getAllReviewers().size());
        stats.put("users", reviewService.getAllUsers().size());
        stats.put("pendingBan", reviewService.getPendingBanUsers().size());
        stats.put("queues", reviewQueueService.getQueueStats());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/scheduler/preview")
    public ResponseEntity<Map<String, Object>> getScanPreview() {
        return ResponseEntity.ok(schedulerService.getScanPreview());
    }

    @PostMapping("/scheduler/scan-timeout")
    public ResponseEntity<Map<String, Object>> scanTimeoutContents(@RequestBody Map<String, Boolean> request) {
        boolean forceExecute = request.getOrDefault("forceExecute", false);
        return ResponseEntity.ok(schedulerService.scanTimeoutContents(forceExecute));
    }

    @PostMapping("/scheduler/scan-appeal-timeout")
    public ResponseEntity<Map<String, Object>> scanAppealTimeout(@RequestBody Map<String, Boolean> request) {
        boolean forceExecute = request.getOrDefault("forceExecute", false);
        return ResponseEntity.ok(schedulerService.scanAppealTimeout(forceExecute));
    }

    @PostMapping("/scheduler/scan-repeat-violations")
    public ResponseEntity<Map<String, Object>> scanRepeatViolations(@RequestBody Map<String, Boolean> request) {
        boolean forceExecute = request.getOrDefault("forceExecute", false);
        return ResponseEntity.ok(schedulerService.scanRepeatViolations(forceExecute));
    }

    @PostMapping("/scheduler/expire-bans")
    public ResponseEntity<Map<String, Object>> autoExpireBans() {
        return ResponseEntity.ok(schedulerService.autoExpireBans());
    }
}
