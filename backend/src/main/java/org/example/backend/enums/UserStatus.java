package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE(0),
    BLOCKED(1);
    @EnumValue
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
