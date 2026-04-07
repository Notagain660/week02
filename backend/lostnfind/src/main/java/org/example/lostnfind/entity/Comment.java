package org.example.lostnfind.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Comment {
    private long commenterId;
    private long replyId;
    private int floor;
    private long postId;
    private String commentText;
    private Date replyTime;

    public Comment(){}
}
