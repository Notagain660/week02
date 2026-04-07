package org.example.lostnfind.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.lostnfind.enums.ChatStatus;

import java.util.Date;

@Getter
@Setter
public class Chat {
    private long senderId;
    private long receiverId;
    private String chatContent;
    private ChatStatus chatStatus;
    private Date sendTime;
    private Date readTime;

    public Chat(){}
}
