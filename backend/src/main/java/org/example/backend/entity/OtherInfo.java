package org.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.UserStatus;

import java.io.Serial;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OtherInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String avatar;
    private String nickname;
    private UserStatus status;
    private int postNum;
}
