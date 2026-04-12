package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.example.backend.enums.CommentStatus;

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
    private Long batchco;

    private Integer floor;

    @TableField("commenterid")
    private Long commenterId;
    @TableField("replyid")
    private Long replyId;
    @TableField("postid")
    private Long postId;
    @TableField("commenttext")
    private String commentText;
    @TableField("commentstatus")
    private CommentStatus commentStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime replyTime;

}
