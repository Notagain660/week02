package org.example.lostnfind.entity;

import org.example.lostnfind.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Post {
    private long posterId;
    private long postId;
    private Type type;
    private PostStatus postStatus;
    private String itemName;
    private String itemPlace;
    private Date itemTime;//find则找到的时间（前端提示），lost则丢失的时间
    private String userDescription;
    private String aiDescription;
    private String itemPhoto;
    private String contact;
    private Visibility visible;
    private boolean pinOrNot;
    private Date postTime;

    public Post(){}
}