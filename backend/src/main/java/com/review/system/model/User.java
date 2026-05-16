package com.review.system.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String username;
    private Integer level;
    private Double creditScore;
    private List<String> violationRecords;
    private Integer violationCount;
    private String status;
    private LocalDateTime banStartTime;
    private LocalDateTime banEndTime;
    private Boolean isPendingBan;
    private String banReason;

    public User() {
        this.creditScore = 100.0;
        this.level = 1;
        this.violationRecords = new ArrayList<>();
        this.violationCount = 0;
        this.status = "NORMAL";
        this.isPendingBan = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Double getCreditScore() { return creditScore; }
    public void setCreditScore(Double creditScore) { this.creditScore = creditScore; }
    public List<String> getViolationRecords() { return violationRecords; }
    public void setViolationRecords(List<String> violationRecords) { this.violationRecords = violationRecords; }
    public Integer getViolationCount() { return violationCount; }
    public void setViolationCount(Integer violationCount) { this.violationCount = violationCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getBanStartTime() { return banStartTime; }
    public void setBanStartTime(LocalDateTime banStartTime) { this.banStartTime = banStartTime; }
    public LocalDateTime getBanEndTime() { return banEndTime; }
    public void setBanEndTime(LocalDateTime banEndTime) { this.banEndTime = banEndTime; }
    public Boolean getIsPendingBan() { return isPendingBan; }
    public void setIsPendingBan(Boolean isPendingBan) { this.isPendingBan = isPendingBan; }
    public String getBanReason() { return banReason; }
    public void setBanReason(String banReason) { this.banReason = banReason; }
}
