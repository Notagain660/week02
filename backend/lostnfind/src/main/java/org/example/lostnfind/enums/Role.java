package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum Role {
    admin(1),
    user(0);
    private final int value;
    Role(int value) {
        this.value = value;
    }
}
