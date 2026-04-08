package org.example.lostnfind.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.example.lostnfind.enums.OurStatus;

@TableName("relation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Relation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("myid")
    private long myId;
    @TableField("itsid")
    private long itsId;
    @TableField("ourstatus")
    private OurStatus ourStatus;
    @TableField(fill = FieldFill.INSERT_UPDATE)//不用手动set时间
    private LocalDateTime requestTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime okTime;

}
