package com.review.system.scheduler;

import com.review.system.model.Content;
import com.review.system.model.User;
import com.review.system.store.DataStore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ReviewScheduler {

    @Scheduled(fixedRate = 60000)
    public void scanTimeoutContents() {
        LocalDateTime now = LocalDateTime.now();
        for (Content content : DataStore.CONTENT_MAP.values()) {
            if ("IN_REVIEW".equals(content.getStatus()) && content.getSubmitTime() != null) {
                long hours = ChronoUnit.HOURS.between(content.getSubmitTime(), now);
                if (hours >= 24) {
                    content.setStatus("AUTO_APPROVED");
                    content.setReviewResult("TIMEOUT_AUTO_APPROVED");
                    content.setReviewTime(now);
                    content.setIsExposed(true);
                }
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void scanAppealTimeout() {
        LocalDateTime now = LocalDateTime.now();
        for (Content content : DataStore.CONTENT_MAP.values()) {
            if (content.getIsAppealed() && !content.getAppealProcessed() && content.getAppealTime() != null) {
                long hours = ChronoUnit.HOURS.between(content.getAppealTime(), now);
                if (hours >= 48) {
                    content.setAppealProcessed(true);
                    content.setStatus("APPROVED");
                    content.setReviewResult("APPEAL_TIMEOUT_AUTO_APPROVED");
                    content.setIsExposed(true);
                }
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void scanRepeatViolations() {
        for (User user : DataStore.USER_MAP.values()) {
            if (user.getViolationCount() >= 5 && !user.getIsPendingBan() && !"BANNED".equals(user.getStatus())) {
                user.setIsPendingBan(true);
                user.setBanReason("Repeat violations: " + user.getViolationCount() + " violations");
                user.setStatus("PENDING_BAN");
            }
        }
    }

    @Scheduled(fixedRate = 300000)
    public void autoExpireBans() {
        LocalDateTime now = LocalDateTime.now();
        for (User user : DataStore.USER_MAP.values()) {
            if ("BANNED".equals(user.getStatus()) && user.getBanEndTime() != null && user.getBanEndTime().isBefore(now)) {
                user.setStatus("NORMAL");
                user.setBanStartTime(null);
                user.setBanEndTime(null);
            }
        }
    }
}
