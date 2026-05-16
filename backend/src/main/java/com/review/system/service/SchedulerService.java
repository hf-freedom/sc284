package com.review.system.service;

import com.review.system.model.Content;
import com.review.system.model.User;
import com.review.system.store.DataStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchedulerService {

    public Map<String, Object> scanTimeoutContents(boolean forceExecute) {
        Map<String, Object> result = new HashMap<>();
        List<Content> processed = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Content content : DataStore.CONTENT_MAP.values()) {
            if ("IN_REVIEW".equals(content.getStatus()) && content.getSubmitTime() != null) {
                long hours = ChronoUnit.HOURS.between(content.getSubmitTime(), now);
                if (hours >= 24 || forceExecute) {
                    content.setStatus("AUTO_APPROVED");
                    content.setReviewResult("TIMEOUT_AUTO_APPROVED");
                    content.setReviewTime(now);
                    content.setIsExposed(true);
                    processed.add(content);
                }
            }
        }
        
        result.put("processedCount", processed.size());
        result.put("processedContents", processed);
        result.put("executeTime", now.toString());
        return result;
    }

    public Map<String, Object> scanAppealTimeout(boolean forceExecute) {
        Map<String, Object> result = new HashMap<>();
        List<Content> processed = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Content content : DataStore.CONTENT_MAP.values()) {
            if (content.getIsAppealed() && !content.getAppealProcessed() && content.getAppealTime() != null) {
                long hours = ChronoUnit.HOURS.between(content.getAppealTime(), now);
                if (hours >= 48 || forceExecute) {
                    content.setAppealProcessed(true);
                    content.setStatus("APPROVED");
                    content.setReviewResult("APPEAL_TIMEOUT_AUTO_APPROVED");
                    content.setIsExposed(true);
                    processed.add(content);
                }
            }
        }
        
        result.put("processedCount", processed.size());
        result.put("processedContents", processed);
        result.put("executeTime", now.toString());
        return result;
    }

    public Map<String, Object> scanRepeatViolations(boolean forceExecute) {
        Map<String, Object> result = new HashMap<>();
        List<User> processed = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (User user : DataStore.USER_MAP.values()) {
            if (user.getViolationCount() >= 5 && !user.getIsPendingBan() && !"BANNED".equals(user.getStatus())) {
                user.setIsPendingBan(true);
                user.setBanReason("Repeat violations: " + user.getViolationCount() + " violations");
                user.setStatus("PENDING_BAN");
                processed.add(user);
            }
        }
        
        result.put("processedCount", processed.size());
        result.put("processedUsers", processed);
        result.put("executeTime", now.toString());
        return result;
    }

    public Map<String, Object> autoExpireBans() {
        Map<String, Object> result = new HashMap<>();
        List<User> processed = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (User user : DataStore.USER_MAP.values()) {
            if ("BANNED".equals(user.getStatus()) && user.getBanEndTime() != null && user.getBanEndTime().isBefore(now)) {
                user.setStatus("NORMAL");
                user.setBanStartTime(null);
                user.setBanEndTime(null);
                processed.add(user);
            }
        }
        
        result.put("processedCount", processed.size());
        result.put("processedUsers", processed);
        result.put("executeTime", now.toString());
        return result;
    }

    public Map<String, Object> getScanPreview() {
        Map<String, Object> preview = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        List<Content> timeoutContents = new ArrayList<>();
        for (Content content : DataStore.CONTENT_MAP.values()) {
            if ("IN_REVIEW".equals(content.getStatus()) && content.getSubmitTime() != null) {
                long hours = ChronoUnit.HOURS.between(content.getSubmitTime(), now);
                timeoutContents.add(content);
            }
        }
        preview.put("timeoutContents", timeoutContents);
        preview.put("timeoutContentCount", timeoutContents.size());
        
        List<Content> appealTimeoutContents = new ArrayList<>();
        for (Content content : DataStore.CONTENT_MAP.values()) {
            if (content.getIsAppealed() && !content.getAppealProcessed()) {
                appealTimeoutContents.add(content);
            }
        }
        preview.put("appealTimeoutContents", appealTimeoutContents);
        preview.put("appealTimeoutContentCount", appealTimeoutContents.size());
        
        List<User> repeatViolationUsers = new ArrayList<>();
        for (User user : DataStore.USER_MAP.values()) {
            if (user.getViolationCount() >= 3 && !user.getIsPendingBan() && !"BANNED".equals(user.getStatus())) {
                repeatViolationUsers.add(user);
            }
        }
        preview.put("repeatViolationUsers", repeatViolationUsers);
        preview.put("repeatViolationUserCount", repeatViolationUsers.size());
        
        List<User> expiringBans = new ArrayList<>();
        for (User user : DataStore.USER_MAP.values()) {
            if ("BANNED".equals(user.getStatus()) && user.getBanEndTime() != null) {
                expiringBans.add(user);
            }
        }
        preview.put("expiringBans", expiringBans);
        preview.put("expiringBanCount", expiringBans.size());
        
        return preview;
    }
}
