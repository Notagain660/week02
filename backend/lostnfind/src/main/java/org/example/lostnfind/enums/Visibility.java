package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum Visibility {
    ALLSEE(0),
    MESEEONLY(1),
    SOMESEE(2);
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
