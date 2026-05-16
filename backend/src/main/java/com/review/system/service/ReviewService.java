package com.review.system.service;

import com.review.system.dto.AppealResultDTO;
import com.review.system.model.Content;
import com.review.system.model.Reviewer;
import com.review.system.model.User;
import com.review.system.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private RiskScoreService riskScoreService;

    public Content reviewContent(String contentId, String reviewerId, String result, String comment) {
        Content content = DataStore.CONTENT_MAP.get(contentId);
        if (content == null) {
            return null;
        }

        Reviewer reviewer = DataStore.REVIEWER_MAP.get(reviewerId);
        if (reviewer != null) {
            reviewer.incrementTodayProcessed();
        }

        content.setReviewerId(reviewerId);
        content.setReviewResult(result);
        content.setReviewComment(comment);
        content.setReviewTime(LocalDateTime.now());

        User user = DataStore.USER_MAP.get(content.getUserId());
        if (user != null) {
            if ("REJECTED".equals(result)) {
                content.setStatus("REJECTED");
                content.setIsExposed(false);
                user.setCreditScore(Math.max(0, user.getCreditScore() - 10));
                user.setViolationCount(user.getViolationCount() + 1);
                user.getViolationRecords().add("Content rejected: " + content.getTitle());
                
                if (user.getViolationCount() >= 3 || content.getRiskScore() >= 80) {
                    user.setIsPendingBan(true);
                    user.setBanReason("Multiple violations or high risk content");
                    user.setStatus("PENDING_BAN");
                }
            } else {
                content.setStatus("APPROVED");
                content.setIsExposed(true);
                user.setCreditScore(Math.min(100, user.getCreditScore() + 1));
            }
        } else {
            content.setStatus("APPROVED".equals(result) ? "APPROVED" : "REJECTED");
            content.setIsExposed("APPROVED".equals(result));
        }

        return content;
    }

    public Content appealContent(String contentId, String appealReason) {
        Content content = DataStore.CONTENT_MAP.get(contentId);
        if (content == null) {
            return null;
        }

        content.setIsAppealed(true);
        content.setAppealReason(appealReason);
        content.setAppealTime(LocalDateTime.now());
        content.setAppealProcessed(false);
        content.setStatus("APPEALED");

        return content;
    }

    public AppealResultDTO processAppeal(String contentId, boolean appealSuccess, String comment) {
        Content content = DataStore.CONTENT_MAP.get(contentId);
        if (content == null) {
            return null;
        }

        AppealResultDTO result = new AppealResultDTO();
        result.setContentId(contentId);
        result.setContentTitle(content.getTitle());
        result.setAppealSuccess(appealSuccess);
        result.setAppealReason(content.getAppealReason());
        result.setProcessComment(comment);
        result.setProcessTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        content.setAppealProcessed(true);

        if (appealSuccess) {
            AppealResultDTO.ContentRestoreInfo restoreInfo = new AppealResultDTO.ContentRestoreInfo();
            restoreInfo.setPreviousStatus(content.getStatus());
            restoreInfo.setPreviousExposed(content.getIsExposed());
            restoreInfo.setPreviousReviewResult(content.getReviewResult());

            content.setStatus("APPROVED");
            content.setIsExposed(true);
            content.setReviewResult("APPEAL_APPROVED");

            restoreInfo.setNewStatus(content.getStatus());
            restoreInfo.setNewExposed(content.getIsExposed());
            restoreInfo.setNewReviewResult(content.getReviewResult());
            result.setContentRestoreInfo(restoreInfo);

            result.addRollbackLog("内容状态: " + restoreInfo.getPreviousStatus() + " → " + restoreInfo.getNewStatus());
            result.addRollbackLog("曝光状态: " + (restoreInfo.isPreviousExposed() ? "已曝光" : "未曝光") + " → " + (restoreInfo.isNewExposed() ? "已曝光" : "未曝光"));
            result.addRollbackLog("审核结果: " + restoreInfo.getPreviousReviewResult() + " → " + restoreInfo.getNewReviewResult());

            User user = DataStore.USER_MAP.get(content.getUserId());
            if (user != null) {
                AppealResultDTO.UserRollbackInfo rollbackInfo = new AppealResultDTO.UserRollbackInfo();
                rollbackInfo.setUserId(user.getId());
                rollbackInfo.setUsername(user.getUsername());
                rollbackInfo.setPreviousCreditScore(user.getCreditScore());
                rollbackInfo.setPreviousViolationCount(user.getViolationCount());
                rollbackInfo.setPreviousStatus(user.getStatus());
                rollbackInfo.setWasPendingBan(user.getIsPendingBan());

                double oldCredit = user.getCreditScore();
                user.setCreditScore(Math.min(100, user.getCreditScore() + 10));
                rollbackInfo.setNewCreditScore(user.getCreditScore());
                rollbackInfo.setCreditScoreChange(user.getCreditScore() - oldCredit);

                int oldViolation = user.getViolationCount();
                if (user.getViolationCount() > 0) {
                    user.setViolationCount(user.getViolationCount() - 1);
                }
                rollbackInfo.setNewViolationCount(user.getViolationCount());

                boolean wasPendingBan = user.getIsPendingBan();
                if (user.getIsPendingBan()) {
                    user.setIsPendingBan(false);
                    user.setStatus("NORMAL");
                    user.setBanReason(null);
                }
                rollbackInfo.setNewStatus(user.getStatus());
                rollbackInfo.setPendingBan(user.getIsPendingBan());
                result.setUserRollbackInfo(rollbackInfo);

                result.addRollbackLog("用户信用分: " + rollbackInfo.getPreviousCreditScore() + " → " + rollbackInfo.getNewCreditScore() + " (+" + rollbackInfo.getCreditScoreChange() + ")");
                result.addRollbackLog("违规次数: " + rollbackInfo.getPreviousViolationCount() + " → " + rollbackInfo.getNewViolationCount());
                if (wasPendingBan) {
                    result.addRollbackLog("封禁状态: 待封禁 → 已解除");
                }
            }
        } else {
            content.setStatus("REJECTED");
            content.setReviewResult("APPEAL_REJECTED");
            result.addRollbackLog("申诉已驳回，维持原有审核结果");
        }

        return result;
    }

    public List<Content> getAppealedContents() {
        List<Content> appealed = new ArrayList<>();
        for (Content content : DataStore.CONTENT_MAP.values()) {
            if (content.getIsAppealed() && !content.getAppealProcessed()) {
                appealed.add(content);
            }
        }
        return appealed;
    }

    public List<User> getPendingBanUsers() {
        List<User> pending = new ArrayList<>();
        for (User user : DataStore.USER_MAP.values()) {
            if (user.getIsPendingBan()) {
                pending.add(user);
            }
        }
        return pending;
    }

    public User processBanApproval(String userId, boolean approved, String comment) {
        User user = DataStore.USER_MAP.get(userId);
        if (user == null) {
            return null;
        }

        if (approved) {
            user.setStatus("BANNED");
            user.setBanStartTime(LocalDateTime.now());
            user.setBanEndTime(LocalDateTime.now().plusDays(30));
        } else {
            user.setStatus("NORMAL");
        }
        user.setIsPendingBan(false);

        return user;
    }

    public List<Reviewer> getAllReviewers() {
        return new ArrayList<>(DataStore.REVIEWER_MAP.values());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(DataStore.USER_MAP.values());
    }
}
