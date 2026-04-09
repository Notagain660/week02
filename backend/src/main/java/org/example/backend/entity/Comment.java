package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("comment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Comment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private long batchco;

    private int floor;

    @TableField("commenterid")
    private long commenterId;
    @TableField("replyid")
    private long replyId;
    @TableField("postid")
    private long postId;
    @TableField("commenttext")
    private String commentText;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime replyTime;

}
