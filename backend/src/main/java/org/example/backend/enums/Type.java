package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum Type {
    LOST(0),
    FIND(1);
    @EnumValue
    private final Integer value;
    Type(Integer value) {
        this.value = value;
    }

    public static Type fromValue(Integer value) {
        for (Type type : Type.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}

