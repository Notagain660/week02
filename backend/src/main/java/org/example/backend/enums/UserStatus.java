package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE(0),
    BLOCKED(1);
    @EnumValue
    @JsonValue
    private final Integer value;
    UserStatus(Integer value) {
        this.value = value;
    }

    public static UserStatus fromValue(Integer value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }

}
