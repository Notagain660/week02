package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ChatStatus {
    NOTYET(0),
    READ(1),
    MEDELETED(2),
    OPDELETED(3);
    @EnumValue
    private final int value;
    ChatStatus(int value) {
        this.value = value;
    }

    public static ChatStatus fromValue(int value) {
        for (ChatStatus chatStatus : ChatStatus.values()) {
            if (chatStatus.value == value) {
                return chatStatus;
            }
        }
        return null;
    }
}
