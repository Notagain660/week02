package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.example.backend.enums.ChatStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("chat")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Chat implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long batchchat;

    @TableField("senderid")
    private long senderId;
    @TableField("receiverid")
    private long receiverId;
    @TableField("chatcontent")
    private String chatContent;
    @TableField("chatstatus")
    private ChatStatus chatStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime sendTime;
    @TableField("readtime")
    private LocalDateTime readTime;

}
