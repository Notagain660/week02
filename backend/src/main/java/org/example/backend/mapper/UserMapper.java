package org.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.backend.entity.User;

public interface UserMapper extends BaseMapper<User> {
    @Update("UPDATE user SET userpostnum = userpostnum + #{delta} WHERE userid = #{userId}")
    int updatePostNum(@Param("userId") Long userId, @Param("delta") int delta);
}
