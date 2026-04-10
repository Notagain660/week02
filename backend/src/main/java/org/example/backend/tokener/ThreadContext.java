package org.example.backend.tokener;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ThreadContext {
    private static final ThreadLocal<UserInfo> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(UserInfo user) {
        currentUser.set(user);
    }

    public static UserInfo getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }

    @Builder
    @NoArgsConstructor
    @Data
    public static class UserInfo {//构造器
        private String userId;
        private String userPhone;
        private String userEmail;
        private String role;

        public UserInfo(String userId, String userEmail, String userPhone, String role) {
            this.userId = userId;
            this.userEmail = userEmail;
            this.userPhone = userPhone;
            this.role = role;
        }

    }

}
