package org.example.lostnfind.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.example.lostnfind.enums.ChatStatus;

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
    private long batchchat;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime readTime;

}
