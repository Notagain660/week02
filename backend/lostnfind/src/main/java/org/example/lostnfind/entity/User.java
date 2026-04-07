package org.example.lostnfind.entity;

import org.example.lostnfind.enums.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class User {
    private Role role;
    private long userId;
    private String userName;
    private String password;
    private String userEmail;
    private String userPhone;
    private int userPostNum;
    private String userAvatar;
    private UserStatus status;

    public User(){}

}
