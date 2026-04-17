package org.example.backend.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.Report;

import java.util.List;

public interface ReportMapper extends BaseMapper<Report> {
    List<Report> selectlist(LambdaQueryWrapper<Report> wrapper);
}
