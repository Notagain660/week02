package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE(0),
    BLOCKED(1);
    private final int value;
    UserStatus(int value) {
        this.value = value;
    }

    public static UserStatus fromValue(int value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }

}
