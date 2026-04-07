package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum PostStatus {
    unFinished(0),
    blocked(1),
    deleted(2),
    completed(3);
    private final int value;
    PostStatus(int value) {
        this.value = value;
    }
}
