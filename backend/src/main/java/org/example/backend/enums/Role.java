package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum Role {
    ADMIN(1),
    USER(0);

    @EnumValue
    private final int value;

    Role(int value) {
        this.value = value;
    }

    public static Role fromValue(int value) {//前端传数字！！！
        for (Role role : Role.values()) {
            if (role.value == value) {
                return role;
            }
        }
        return null;
    }
}
