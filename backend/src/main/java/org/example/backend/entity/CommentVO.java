package org.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentVO  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer floor;
    private Long commenterId;
    private String commentText;
    private Long postId;
    private Long replyId;
    private Long batchco;
}
