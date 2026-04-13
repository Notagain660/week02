package org.example.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ReportType {
    USER(0),
    POST(1),
    COMMENT(2);
    @EnumValue
    private final Integer value;
    ReportType(Integer value) {
        this.value = value;
    }
    public static ReportType fromValue(Integer value) {
        for (ReportType status : ReportType.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }

}
