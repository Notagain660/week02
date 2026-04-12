package org.example.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.backend.controller.Controller;
import org.example.backend.entity.Comment;
import org.example.backend.entity.Post;
import org.example.backend.entity.User;
import org.example.backend.enums.CommentStatus;
import org.example.backend.enums.StatusCode;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.mapper.PostMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    public boolean sendComment(Long replyId, Long postId, String text){
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        //用户校验
        if (userMe == null){
            throw new BusiException(StatusCode.USERNOEXIST);
        }
        if(replyId == null || replyId == 0){
            throw new BusiException(StatusCode.USERNOEXIST);
        }
        //校验被回复用户是不是在评论区发言过或者是帖主
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCommenterId, replyId);
        //查有没有发过评论
        Comment replyComment = commentMapper.selectOne(wrapper);
        if (replyComment != null){//发过，就回复别人的评论
            Long opPostId = replyComment.getPostId();//评论是不是同一个帖子下
            if(postId == null || opPostId == null) {//防黑客的
                throw new BusiException(StatusCode.NOPOST);
            }
            if (!postId.equals(opPostId)) {//不在一个帖子下
                throw new BusiException(StatusCode.INVALID, "回复的评论不属于该帖子");
            }
        } else {//回复帖主的
            LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
            postWrapper.eq(Post::getPosterId, replyId);

            Post post = postMapper.selectOne(postWrapper);
            if (post == null){
                throw new BusiException(StatusCode.NOPOST);
            }
        }

        Integer maxFloor = commentMapper.selectMaxFloorForUpdate(postId);//楼层分帖子按条增加
        int newFloor = (maxFloor == null) ? 1 : maxFloor + 1;
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setReplyId(replyId);
        comment.setCommenterId(userMe.getUserId());
        comment.setCommentText(text);
        comment.setFloor(newFloor);
        comment.setCommentStatus(CommentStatus.ACTIVE);
        comment.setReplyTime(LocalDateTime.now());
        commentMapper.insert(comment);
        return true;
    }

    public boolean deleteComment(Long commentId){
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        Comment comment = commentMapper.selectById(commentId);
        if(userMe == null){
            throw new BusiException(StatusCode.USERNOEXIST);
        }
        if(!userMe.getUserId().equals(comment.getCommenterId())){
            throw new BusiException(StatusCode.INVALID);
        }
        comment.setCommentStatus(CommentStatus.DELETED);
        commentMapper.updateById(comment);
        return true;
    }


}
