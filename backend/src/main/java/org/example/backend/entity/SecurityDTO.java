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
public class SecurityDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String str;//修改密码时是新密码，其余类推，知三改一
    private String passerword;
    private String phoner;
    private String emailer;//加er跟其它字段区分开来因为这是专门用来接收传给安全方法的数据包装）
}
