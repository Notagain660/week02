package org.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.example.backend.entity.Post;

public interface PostMapper extends BaseMapper<Post> {

        Page<Post> selectVisiblePosts(Page<Post> page,
                                      @Param("userMeId") Long userMeId,
                                      @Param("posterId") Long posterId,
                                      @Param("type") Integer type,
                                      @Param("status") Integer status,
                                      @Param("itemName") String itemName,
                                      @Param("itemPlace") String itemPlace);
}
