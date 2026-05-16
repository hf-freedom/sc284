package com.review.system.model;

import java.time.LocalDateTime;

public class Content {
    private String id;
    private String userId;
    private String title;
    private String content;
    private String type;
    private Integer reportCount;
    private Double riskScore;
    private String status;
    private String reviewerId;
    private String reviewResult;
    private String reviewComment;
    private LocalDateTime submitTime;
    private LocalDateTime reviewTime;
    private Boolean isAppealed;
    private String appealReason;
    private LocalDateTime appealTime;
    private Boolean appealProcessed;
    private Boolean isExposed;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getReportCount() { return reportCount; }
    public void setReportCount(Integer reportCount) { this.reportCount = reportCount; }
    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }
    public String getReviewResult() { return reviewResult; }
    public void setReviewResult(String reviewResult) { this.reviewResult = reviewResult; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public LocalDateTime getReviewTime() { return reviewTime; }
    public void setReviewTime(LocalDateTime reviewTime) { this.reviewTime = reviewTime; }
    public Boolean getIsAppealed() { return isAppealed; }
    public void setIsAppealed(Boolean isAppealed) { this.isAppealed = isAppealed; }
    public String getAppealReason() { return appealReason; }
    public void setAppealReason(String appealReason) { this.appealReason = appealReason; }
    public LocalDateTime getAppealTime() { return appealTime; }
    public void setAppealTime(LocalDateTime appealTime) { this.appealTime = appealTime; }
    public Boolean getAppealProcessed() { return appealProcessed; }
    public void setAppealProcessed(Boolean appealProcessed) { this.appealProcessed = appealProcessed; }
    public Boolean getIsExposed() { return isExposed; }
    public void setIsExposed(Boolean isExposed) { this.isExposed = isExposed; }
}
