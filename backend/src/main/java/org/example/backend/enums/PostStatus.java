package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PostStatus {
    UNFINISHED(0),
    BLOCKED(1),
    DELETED(2),
    COMPLETED(3);
    @EnumValue
    private final Integer value;
    PostStatus(Integer value) {
        this.value = value;
    }

    public static PostStatus fromValue(Integer value) {
        for (PostStatus postStatus : PostStatus.values()) {
            if (postStatus.value.equals(value)) {
                return postStatus;
            }
        }
        return null;
    }
}