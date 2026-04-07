package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum Visibility {
    allSee(0),
    meSeeOnly(1),
    someSee(2);
    private final int value;
    Visibility(int value) {
        this.value = value;
    }
}
