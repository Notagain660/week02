package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    active(0),
    blocked(1);
    UserStatus(int value) {
        this.value = value;
    }
    private final int value;
}
