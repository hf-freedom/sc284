package com.review.system.service;

import com.review.system.model.Content;
import com.review.system.model.Reviewer;
import com.review.system.model.User;
import com.review.system.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReviewQueueService {

    @Autowired
    private RiskScoreService riskScoreService;

    @Autowired
    private LoadBalanceService loadBalanceService;

    public Content submitContent(Content content) {
        content.setId(UUID.randomUUID().toString());
        content.setSubmitTime(LocalDateTime.now());
        content.setReportCount(0);
        content.setStatus("PENDING");
        content.setIsAppealed(false);
        content.setAppealProcessed(false);
        content.setIsExposed(false);

        User user = DataStore.USER_MAP.get(content.getUserId());
        if (user == null) {
            user = new User();
            user.setId(content.getUserId());
            user.setUsername("User_" + content.getUserId());
            DataStore.USER_MAP.put(user.getId(), user);
        }

        double riskScore = riskScoreService.calculateRiskScore(content, user);
        content.setRiskScore(riskScore);

        if (riskScoreService.shouldAutoApprove(riskScore)) {
            content.setStatus("APPROVED");
            content.setReviewResult("AUTO_APPROVED");
            content.setReviewTime(LocalDateTime.now());
            content.setIsExposed(true);
        } else if (riskScoreService.requiresManualReview(riskScore)) {
            String type = content.getType() != null ? content.getType() : "TEXT";
            Queue<Content> queue = DataStore.REVIEW_QUEUES.get(type);
            if (queue == null) {
                queue = DataStore.REVIEW_QUEUES.get("TEXT");
            }
            queue.offer(content);
            content.setStatus("IN_REVIEW");
        } else {
            String type = content.getType() != null ? content.getType() : "TEXT";
            Queue<Content> queue = DataStore.REVIEW_QUEUES.get(type);
            if (queue == null) {
                queue = DataStore.REVIEW_QUEUES.get("TEXT");
            }
            queue.offer(content);
            content.setStatus("IN_REVIEW");
        }

        DataStore.CONTENT_MAP.put(content.getId(), content);
        return content;
    }

    public Content getNextReviewTask(String reviewerId) {
        Reviewer reviewer = DataStore.REVIEWER_MAP.get(reviewerId);
        if (reviewer == null || !reviewer.canProcessMore()) {
            return null;
        }

        return loadBalanceService.assignNextTask(reviewer);
    }

    public Content getNextReviewTaskByType(String reviewerId, String type) {
        Reviewer reviewer = DataStore.REVIEWER_MAP.get(reviewerId);
        if (reviewer == null || !reviewer.canProcessMore()) {
            return null;
        }

        return loadBalanceService.assignNextTaskByType(reviewer, type);
    }

    public Map<String, Integer> getQueueStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<String, Queue<Content>> entry : DataStore.REVIEW_QUEUES.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        stats.put("TOTAL", DataStore.CONTENT_MAP.size());
        return stats;
    }

    public List<Content> getPendingContents() {
        List<Content> pending = new ArrayList<>();
        for (Content content : DataStore.CONTENT_MAP.values()) {
            if ("IN_REVIEW".equals(content.getStatus())) {
                pending.add(content);
            }
        }
        return pending;
    }

    public Content getContentById(String id) {
        return DataStore.CONTENT_MAP.get(id);
    }

    public List<Content> getAllContents() {
        return new ArrayList<>(DataStore.CONTENT_MAP.values());
    }
}
