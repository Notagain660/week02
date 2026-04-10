package org.example.backend.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@Data

public class LoginReturnData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String token;
    private User user;

    public LoginReturnData(String token, User user) {
        this.token = token;
        this.user = user;
    }

}
