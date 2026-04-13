package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ReportStatus {
    NOT(0),
    ING(1),
    OK(2);
    @EnumValue
    private final Integer value;
    ReportStatus(Integer value) {
        this.value = value;
    }
    public static ReportStatus fromValue(Integer value) {
        for (ReportStatus status : ReportStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }

}
