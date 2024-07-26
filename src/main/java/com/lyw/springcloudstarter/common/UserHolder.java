package com.lyw.springcloudstarter.common;

/**
 * @author: liuyaowen
 * @poject: springboot-starter
 * @create: 2024-07-22 13:31
 * @Description:
 */
public class UserHolder {

    private static final ThreadLocal<Long> USER_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_CONSTANT_HOLDER = new ThreadLocal<>();

    public static void setUserId(Long user) {
        USER_HOLDER.set(user);
    }

    public static Long getUserID() {
        return USER_HOLDER.get();
    }

    public static void setUserRole(String userConstant) {
        USER_CONSTANT_HOLDER.set(userConstant);
    }

    public static String getUserRole() {
        return USER_CONSTANT_HOLDER.get();
    }

    public static void remove() {
        USER_HOLDER.remove();
        USER_CONSTANT_HOLDER.remove();
    }
}
