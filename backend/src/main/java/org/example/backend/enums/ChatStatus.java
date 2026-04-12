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
    private final Integer value;
    ChatStatus(Integer value) {
        this.value = value;
    }

    public static ChatStatus fromValue(Integer value) {
        for (ChatStatus chatStatus : ChatStatus.values()) {
            if (chatStatus.value.equals(value)) {
                return chatStatus;
            }
        }
        return null;
    }
}
