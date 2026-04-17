package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.ReportStatus;
import org.example.backend.enums.ReportType;

import java.io.Serial;
import java.io.Serializable;
@TableName("report")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Report implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "batchre", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long batchre;
    @TableField("reporter")
    private Long reporter;
    @TableField("reportee")
    private Long reportee;
    @TableField("type")
    private ReportType reportType;
    @TableField("contentid")
    private Long contentId;
    @TableField("reason")
    private String reason;
    @TableField("status")
    private ReportStatus status;

}

