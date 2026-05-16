package com.review.system.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Reviewer {
    private String id;
    private String name;
    private Integer dailyLimit;
    private Map<LocalDate, Integer> dailyProcessed;
    private String status;

    public Reviewer() {
        this.dailyProcessed = new HashMap<>();
        this.status = "ACTIVE";
    }

    public Integer getTodayProcessed() {
        return dailyProcessed.getOrDefault(LocalDate.now(), 0);
    }

    public void incrementTodayProcessed() {
        LocalDate today = LocalDate.now();
        dailyProcessed.put(today, dailyProcessed.getOrDefault(today, 0) + 1);
    }

    public boolean canProcessMore() {
        return getTodayProcessed() < dailyLimit;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; }
    public Map<LocalDate, Integer> getDailyProcessed() { return dailyProcessed; }
    public void setDailyProcessed(Map<LocalDate, Integer> dailyProcessed) { this.dailyProcessed = dailyProcessed; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
