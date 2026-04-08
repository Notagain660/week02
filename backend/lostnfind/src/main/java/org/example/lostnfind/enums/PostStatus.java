package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum PostStatus {
    UNFINISHED(0),
    BLOCKED(1),
    DELETED(2),
    COMPLETED(3);
    private final int value;
    PostStatus(int value) {
        this.value = value;
    }

    public static PostStatus fromValue(int value) {
        for (PostStatus postStatus : PostStatus.values()) {
            if (postStatus.value == value) {
                return postStatus;
            }
        }
        return null;
    }
}
