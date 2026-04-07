package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum OurStatus {
    noKnit(0),
    requesting(1),
    knitOK(2);
    private final int value;
    OurStatus(int value) {
        this.value = value;
    }
}
