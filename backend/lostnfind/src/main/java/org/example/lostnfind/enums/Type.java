package org.example.lostnfind.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Type {
    lost(0),
    find(1);
    private final int value;
    Type(int value) {
        this.value = value;
    }
}
