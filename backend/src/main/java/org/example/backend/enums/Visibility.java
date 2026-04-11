package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum Visibility {
    ALLSEE(0),
    MESEEONLY(1),
    SOMESEE(2);
    @EnumValue
    private final int value;
    Visibility(int value) {
        this.value = value;
    }

    public static Visibility fromValue(int value) {
        for (Visibility visibility : Visibility.values()) {
            if (visibility.value == value) {
                return visibility;
            }
        }
        return null;
    }
}