package com.review.system.store;

import com.review.system.model.Content;
import com.review.system.model.Reviewer;
import com.review.system.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    public static final Map<String, Content> CONTENT_MAP = new ConcurrentHashMap<>();
    public static final Map<String, User> USER_MAP = new ConcurrentHashMap<>();
    public static final Map<String, Reviewer> REVIEWER_MAP = new ConcurrentHashMap<>();
    public static final Map<String, Queue<Content>> REVIEW_QUEUES = new ConcurrentHashMap<>();
    public static final Set<String> RISK_KEYWORDS = new HashSet<>();

    static {
        REVIEW_QUEUES.put("TEXT", new LinkedList<>());
        REVIEW_QUEUES.put("IMAGE", new LinkedList<>());
        REVIEW_QUEUES.put("VIDEO", new LinkedList<>());
        REVIEW_QUEUES.put("COMMENT", new LinkedList<>());

        RISK_KEYWORDS.add("赌博");
        RISK_KEYWORDS.add("色情");
        RISK_KEYWORDS.add("暴力");
        RISK_KEYWORDS.add("诈骗");
        RISK_KEYWORDS.add("毒品");
        RISK_KEYWORDS.add("恐怖");
        RISK_KEYWORDS.add("反动");

        Reviewer reviewer1 = new Reviewer();
        reviewer1.setId("r1");
        reviewer1.setName("审核员张三");
        reviewer1.setDailyLimit(50);
        REVIEWER_MAP.put(reviewer1.getId(), reviewer1);

        Reviewer reviewer2 = new Reviewer();
        reviewer2.setId("r2");
        reviewer2.setName("审核员李四");
        reviewer2.setDailyLimit(50);
        REVIEWER_MAP.put(reviewer2.getId(), reviewer2);

        Reviewer reviewer3 = new Reviewer();
        reviewer3.setId("r3");
        reviewer3.setName("审核员王五");
        reviewer3.setDailyLimit(50);
        REVIEWER_MAP.put(reviewer3.getId(), reviewer3);

        User user1 = new User();
        user1.setId("u1");
        user1.setUsername("普通用户");
        user1.setLevel(1);
        USER_MAP.put(user1.getId(), user1);

        User user2 = new User();
        user2.setId("u2");
        user2.setUsername("VIP用户");
        user2.setLevel(5);
        user2.setCreditScore(95.0);
        USER_MAP.put(user2.getId(), user2);
    }
}
