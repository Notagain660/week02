package org.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatisticsMapper extends BaseMapper<Void> {
    List<Long> selectActive (@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    Integer selectPosts();
    Integer selectFound();
    List<Map<String, Object>> selectItems();
    List<Map<String, Object>> selectPlaces();
}
