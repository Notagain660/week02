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
    private long postId;
    @TableField("posterid")
    private long posterId;

    private Type type;
    private String contact;
    private Visibility visible;
    @TableField("poststatus")
    private PostStatus postStatus;
    @TableField("itemname")
    private String itemName;
    @TableField("itemplace")
    private String itemPlace;
    @TableField(fill =  FieldFill.DEFAULT)
    private LocalDateTime itemTime;//find则找到的时间（前端提示），lost则丢失的时间
    @TableField("userdes")
    private String userDescription;
    @TableField("aides")
    private String aiDescription;
    @TableField("itemphoto")
    private String itemPhoto;
    @TableField("pinornot")
    private boolean pinOrNot;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime postTime;

}
