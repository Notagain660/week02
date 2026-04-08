package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum OurStatus {
    NOKNIT(0),
    REQUESTING(1),
    kNITOK(2);
    private final int value;
    OurStatus(int value) {
        this.value = value;
    }

    public static OurStatus fromValue(int value) {
        for (OurStatus status : OurStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
