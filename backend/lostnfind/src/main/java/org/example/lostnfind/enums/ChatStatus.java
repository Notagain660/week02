package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum ChatStatus {
    notYet(0),
    read(1),
    userDeleted(2);
    private final int value;
    ChatStatus(int value) {
        this.value = value;
    }
}
