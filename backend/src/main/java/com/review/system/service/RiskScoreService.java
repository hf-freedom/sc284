package com.review.system.service;

import com.review.system.model.Content;
import com.review.system.model.User;
import com.review.system.store.DataStore;
import org.springframework.stereotype.Service;

@Service
public class RiskScoreService {

    public double calculateRiskScore(Content content, User user) {
        double score = 0;

        score += calculateKeywordRisk(content);
        score += calculateReportRisk(content);
        score += calculateUserLevelRisk(user);
        score += calculateCreditRisk(user);
        score += calculateViolationHistoryRisk(user);

        return Math.min(score, 100);
    }

    private double calculateKeywordRisk(Content content) {
        String text = content.getTitle() + " " + content.getContent();
        int keywordCount = 0;
        for (String keyword : DataStore.RISK_KEYWORDS) {
            if (text.toLowerCase().contains(keyword.toLowerCase())) {
                keywordCount++;
            }
        }
        return keywordCount * 20;
    }

    private double calculateReportRisk(Content content) {
        int reportCount = content.getReportCount() != null ? content.getReportCount() : 0;
        return Math.min(reportCount * 10, 30);
    }

    private double calculateUserLevelRisk(User user) {
        int level = user.getLevel();
        if (level >= 5) return 5;
        if (level >= 3) return 10;
        return 15;
    }

    private double calculateCreditRisk(User user) {
        double credit = user.getCreditScore();
        if (credit >= 90) return 5;
        if (credit >= 70) return 15;
        if (credit >= 50) return 25;
        return 35;
    }

    private double calculateViolationHistoryRisk(User user) {
        int violationCount = user.getViolationCount();
        if (violationCount >= 5) return 40;
        if (violationCount >= 3) return 25;
        if (violationCount >= 1) return 15;
        return 0;
    }

    public String getRiskLevel(double riskScore) {
        if (riskScore < 30) return "LOW";
        if (riskScore < 60) return "MEDIUM";
        return "HIGH";
    }

    public boolean shouldAutoApprove(double riskScore) {
        return riskScore < 30;
    }

    public boolean requiresManualReview(double riskScore) {
        return riskScore >= 60;
    }
}
