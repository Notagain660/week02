package org.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.PostStatus;
import org.example.backend.enums.Type;

import java.io.Serial;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Type type;
    private PostStatus status;
    private String itemName;
}
