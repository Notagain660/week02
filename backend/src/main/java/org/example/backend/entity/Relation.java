package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.example.backend.enums.OurStatus;

@TableName("relation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Relation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("myid")
    private Long myId;
    @TableField("itsid")
    private Long itsId;
    @TableField("ourstatus")
    private OurStatus ourStatus;
    @TableField(fill = FieldFill.INSERT_UPDATE)//不用手动set时间
    private LocalDateTime requestTime;
    @TableField("oktime")
    private LocalDateTime okTime;

}
