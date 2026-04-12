package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OurStatus {
    NOKNIT(0),
    REQUESTING(1),
    kNITOK(2);
    @EnumValue
    private final Integer value;
    OurStatus(Integer value) {
        this.value = value;
    }

    public static OurStatus fromValue(Integer value) {
        for (OurStatus status : OurStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}

