package org.example.backend.enums;

import lombok.Getter;

@Getter
public enum ChatStatus {
    NOTYET(0),
    READ(1),
    USERDELETED(2);
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
