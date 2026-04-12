package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum CommentStatus {
    ACTIVE(0),
    BLOCKED(1),
    DELETED(2);
    @EnumValue
    private final Integer value;
    CommentStatus(Integer value) {
        this.value = value;
    }
    public static CommentStatus fromValue(Integer value) {
        for (CommentStatus commentStatus : CommentStatus.values()) {
            if (commentStatus.value.equals(value)) {
                return commentStatus;
            }
        }
        return null;
    }
}
