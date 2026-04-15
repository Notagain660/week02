package org.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.backend.entity.Comment;

public interface CommentMapper  extends BaseMapper<Comment> {
    @Select("SELECT MAX(floor) FROM comment WHERE postid = #{postId} FOR UPDATE")
    Integer selectMaxFloorForUpdate(@Param("postId") Long postId);
    //悲观锁,事务隔离级别使用 REPEATABLE_READ（MySQL 默认），FOR UPDATE 会锁住相关行。
    //
    //如果评论表数据量很大，FOR UPDATE 可能会影响性能

}