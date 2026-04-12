package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum Visibility {
    ALLSEE(0),
    MESEEONLY(1),
    FRIENDSEE(2);
    @EnumValue
    private final Integer value;
    Visibility(Integer value) {
        this.value = value;
    }

    public static Visibility fromValue(Integer value) {
        for (Visibility visibility : Visibility.values()) {
            if (visibility.value.equals(value)) {
                return visibility;
            }
        }
        return null;
    }
}