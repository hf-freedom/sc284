package com.review.system.service;

import com.review.system.model.Content;
import com.review.system.model.Reviewer;
import com.review.system.store.DataStore;
import org.springframework.stereotype.Service;

import java.util.Queue;

@Service
public class LoadBalanceService {

    public Content assignNextTask(Reviewer reviewer) {
        String[] types = {"TEXT", "COMMENT", "IMAGE", "VIDEO"};
        
        for (String type : types) {
            Queue<Content> queue = DataStore.REVIEW_QUEUES.get(type);
            if (queue != null && !queue.isEmpty()) {
                Content content = queue.peek();
                if (content != null && content.getReviewerId() == null) {
                    content.setReviewerId(reviewer.getId());
                    return queue.poll();
                }
            }
        }
        
        for (String type : types) {
            Queue<Content> queue = DataStore.REVIEW_QUEUES.get(type);
            if (queue != null && !queue.isEmpty()) {
                return queue.poll();
            }
        }
        
        return null;
    }

    public Content assignNextTaskByType(Reviewer reviewer, String type) {
        Queue<Content> queue = DataStore.REVIEW_QUEUES.get(type);
        if (queue != null && !queue.isEmpty()) {
            Content content = queue.peek();
            if (content != null && content.getReviewerId() == null) {
                content.setReviewerId(reviewer.getId());
                return queue.poll();
            }
            return queue.poll();
        }
        return null;
    }

    public Reviewer findAvailableReviewer() {
        for (Reviewer reviewer : DataStore.REVIEWER_MAP.values()) {
            if ("ACTIVE".equals(reviewer.getStatus()) && reviewer.canProcessMore()) {
                return reviewer;
            }
        }
        return null;
    }
}
