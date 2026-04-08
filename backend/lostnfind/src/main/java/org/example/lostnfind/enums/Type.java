package org.example.lostnfind.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Type {
    LOST(0),
    FIND(1);
    private final int value;
    Type(int value) {
        this.value = value;
    }

    public static Type fromValue(int value) {
        for (Type type : Type.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
