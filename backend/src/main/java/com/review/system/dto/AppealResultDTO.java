package com.review.system.dto;

import java.util.ArrayList;
import java.util.List;

public class AppealResultDTO {
    private String contentId;
    private String contentTitle;
    private boolean appealSuccess;
    private String appealReason;
    private String processComment;
    private String processTime;
    
    private ContentRestoreInfo contentRestoreInfo;
    private UserRollbackInfo userRollbackInfo;
    private List<String> rollbackLogs;
    
    public AppealResultDTO() {
        this.rollbackLogs = new ArrayList<>();
    }
    
    public void addRollbackLog(String log) {
        this.rollbackLogs.add(log);
    }
    
    public static class ContentRestoreInfo {
        private String previousStatus;
        private String newStatus;
        private boolean previousExposed;
        private boolean newExposed;
        private String previousReviewResult;
        private String newReviewResult;
        
        public String getPreviousStatus() { return previousStatus; }
        public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
        public String getNewStatus() { return newStatus; }
        public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
        public boolean isPreviousExposed() { return previousExposed; }
        public void setPreviousExposed(boolean previousExposed) { this.previousExposed = previousExposed; }
        public boolean isNewExposed() { return newExposed; }
        public void setNewExposed(boolean newExposed) { this.newExposed = newExposed; }
        public String getPreviousReviewResult() { return previousReviewResult; }
        public void setPreviousReviewResult(String previousReviewResult) { this.previousReviewResult = previousReviewResult; }
        public String getNewReviewResult() { return newReviewResult; }
        public void setNewReviewResult(String newReviewResult) { this.newReviewResult = newReviewResult; }
    }
    
    public static class UserRollbackInfo {
        private String userId;
        private String username;
        private double previousCreditScore;
        private double newCreditScore;
        private double creditScoreChange;
        private int previousViolationCount;
        private int newViolationCount;
        private String previousStatus;
        private String newStatus;
        private boolean wasPendingBan;
        private boolean isPendingBan;
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public double getPreviousCreditScore() { return previousCreditScore; }
        public void setPreviousCreditScore(double previousCreditScore) { this.previousCreditScore = previousCreditScore; }
        public double getNewCreditScore() { return newCreditScore; }
        public void setNewCreditScore(double newCreditScore) { this.newCreditScore = newCreditScore; }
        public double getCreditScoreChange() { return creditScoreChange; }
        public void setCreditScoreChange(double creditScoreChange) { this.creditScoreChange = creditScoreChange; }
        public int getPreviousViolationCount() { return previousViolationCount; }
        public void setPreviousViolationCount(int previousViolationCount) { this.previousViolationCount = previousViolationCount; }
        public int getNewViolationCount() { return newViolationCount; }
        public void setNewViolationCount(int newViolationCount) { this.newViolationCount = newViolationCount; }
        public String getPreviousStatus() { return previousStatus; }
        public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
        public String getNewStatus() { return newStatus; }
        public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
        public boolean isWasPendingBan() { return wasPendingBan; }
        public void setWasPendingBan(boolean wasPendingBan) { this.wasPendingBan = wasPendingBan; }
        public boolean isPendingBan() { return isPendingBan; }
        public void setPendingBan(boolean pendingBan) { isPendingBan = pendingBan; }
    }
    
    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }
    public String getContentTitle() { return contentTitle; }
    public void setContentTitle(String contentTitle) { this.contentTitle = contentTitle; }
    public boolean isAppealSuccess() { return appealSuccess; }
    public void setAppealSuccess(boolean appealSuccess) { this.appealSuccess = appealSuccess; }
    public String getAppealReason() { return appealReason; }
    public void setAppealReason(String appealReason) { this.appealReason = appealReason; }
    public String getProcessComment() { return processComment; }
    public void setProcessComment(String processComment) { this.processComment = processComment; }
    public String getProcessTime() { return processTime; }
    public void setProcessTime(String processTime) { this.processTime = processTime; }
    public ContentRestoreInfo getContentRestoreInfo() { return contentRestoreInfo; }
    public void setContentRestoreInfo(ContentRestoreInfo contentRestoreInfo) { this.contentRestoreInfo = contentRestoreInfo; }
    public UserRollbackInfo getUserRollbackInfo() { return userRollbackInfo; }
    public void setUserRollbackInfo(UserRollbackInfo userRollbackInfo) { this.userRollbackInfo = userRollbackInfo; }
    public List<String> getRollbackLogs() { return rollbackLogs; }
    public void setRollbackLogs(List<String> rollbackLogs) { this.rollbackLogs = rollbackLogs; }
}
