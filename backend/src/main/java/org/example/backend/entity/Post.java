package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.example.backend.enums.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("post")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Post implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "postid", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long postId;
    @TableField("posterid")
    private Long posterId;

    private Type type;
    private String contact;
    private Visibility visible;
    @TableField("poststatus")
    private PostStatus postStatus;
    @TableField("itemname")
    private String itemName;
    @TableField("itemplace")
    private String itemPlace;
    @TableField("itemtime")
    private LocalDateTime itemTime;//find则找到的时间（前端提示），lost则丢失的时间
    @TableField("userdes")
    private String userDescription;
    @TableField("aides")
    private String aiDescription;//前端不提供用户输入这个的界面
    @TableField("itemphoto")
    private String itemPhoto;
    @TableField("pinornot")
    private boolean pinOrNot;//前端不提供用户输入这个的界面，反正默认为false
    @TableField("posttime")
    private LocalDateTime postTime;
    @TableField("top_expire_time")
    private LocalDateTime topExpireTime;

}
