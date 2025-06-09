package com.common.userInfo.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserContext {
    private static final Map<String, String> userContextConcurrentHashMap = new ConcurrentHashMap<>();

    public static void setUserId(String userId) {
        userContextConcurrentHashMap.put("userId", userId);
    }

    public static void setRoleId(String id) {
        userContextConcurrentHashMap.put("roleId", id);
    }

    public static String getUserId() {
        return userContextConcurrentHashMap.get("userId");
    }

    public static String getRoleId() {
        return userContextConcurrentHashMap.get("roleId");
    }

    public static void remove() {
        userContextConcurrentHashMap.clear();
    }
}
