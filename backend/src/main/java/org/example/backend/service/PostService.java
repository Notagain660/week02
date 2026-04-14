package org.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Post;
import org.example.backend.entity.PostDTO;
import org.example.backend.entity.User;
import org.example.backend.enums.PostStatus;
import org.example.backend.enums.StatusCode;
import org.example.backend.enums.UserStatus;
import org.example.backend.mapper.PostMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class PostService {

    private final UserService userService;
    private final AIService aiService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    public String getImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {//空图片校验
            throw new BusiException(StatusCode.INVALID, "请选择图片");
        }

        String extendName = userService.getString(image);

        String fileName = UUID.randomUUID() + extendName;//生成安全的文件名
        String uploadD = "D:/uploads/post/";//准备目录
        File file = new File(uploadD + fileName);
        //保存（本地d盘已经建了目录所以没必要再判断和创建目录了）
        try {
            image.transferTo(file);//上传的临时文件直接写入到指定的目标文件，目录不存在爆IOException
        } catch (IOException e) {
            log.error("头像上传失败", e);
            throw new BusiException(StatusCode.DBERROR, "图片保存失败");
        }

        return "/post/" + fileName;
    }

    public boolean post(Post post) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if (userMe == null) {
            throw new BusiException(StatusCode.USERNOEXIST);
        }

        if(userMe.getStatus().equals(UserStatus.BLOCKED)){
            throw new BusiException(StatusCode.USERBLOCKED, "禁止发布帖子");
        }
        String AIDescription = aiService.generateDescription(post.getItemName(), post.getItemPlace()
                , post.getUserDescription(), post.getItemTime());
        post.setAiDescription(AIDescription);
        post.setPosterId(userMe.getUserId());
        post.setPostStatus(PostStatus.UNFINISHED);
        post.setPinOrNot(false);

        LocalDateTime itemTime = post.getItemTime();
        itemTime = itemTime.withSecond(0).withNano(0);
        post.setItemTime(itemTime);
        if (postMapper.insert(post) == 1) {
            userMapper.updatePostNum(post.getPosterId(), 1);
            return true;
        }
        return false;
    }

    public IPage<PostDTO> browsePost(int pageCode, int pageSize,
                                     Integer type, Integer status, String itemName, String itemPlace){
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        Page<Post> page = new Page<>(pageCode, pageSize);//创建MyBatis-Plus的分页对象，指定页码和每页大小
        //调用自定义的 Mapper方法 selectVisiblePosts。
        //该方法内部会执行一条分页 SQL，根据currentUserId、type、status、itemName过滤帖子，并只返回当前用户可见的帖子。
        Page<Post> resultPage = postMapper.selectVisiblePosts(page, userMe.getUserId()
                , type, status, itemName, itemPlace);
        //Page<T>的convert方法返回的是IPage<PostDTO>（接口），而不是Page<PostDTO>（实现类）,IPage和Page序列化后是一样的。
        return resultPage.convert(post ->
                new PostDTO(post.getType(), post.getPostStatus(), post.getItemName(), post.getItemPlace()));
    }

    public boolean updatePost(Post post) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if (userMe == null) {
            throw new BusiException(StatusCode.USERNOEXIST);
        }
        if(userMe.getStatus().equals(UserStatus.BLOCKED)){
            throw new BusiException(StatusCode.USERBLOCKED);
        }
        if(!Objects.equals(userMe.getUserId(), post.getPosterId())){
            throw new BusiException(StatusCode.INVALID);
        }

        Post posttest = postMapper.selectById(post.getPostId());
        if(!posttest.getPostStatus().equals(PostStatus.UNFINISHED)){
            throw new BusiException(StatusCode.INVALID);
        }

        Post post2 = postMapper.selectById(post.getPostId());
        post.setType(post2.getType());//类型不能改
        String AIDescription = aiService.generateDescription(post.getItemName(), post.getItemPlace()
                , post.getUserDescription(), post.getItemTime());
        post.setAiDescription(AIDescription);
        post.setPostTime(LocalDateTime.now());
        return postMapper.updateById(post) == 1;
    }

    public boolean deletePost(Long postId){
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if (userMe == null) {
            throw new BusiException(StatusCode.USERNOEXIST);
        }

        Post post = postMapper.selectById(postId);
        if(!Objects.equals(userMe.getUserId(), post.getPosterId())){
            throw new BusiException(StatusCode.INVALID);
        }
        post.setPostStatus(PostStatus.DELETED);
        if(postMapper.updateById(post) == 1){
            userMapper.updatePostNum(post.getPosterId(), -1);
            return true;
        }
        return false;
    }

    public Post checkPost(Long postId) {
        Post post = postMapper.selectById(postId);
        if(post == null){
            throw new BusiException(StatusCode.INVALID);
        } else {
            return post;
        }
    }

}
