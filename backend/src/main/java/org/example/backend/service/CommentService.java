package org.example.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Comment;
import org.example.backend.entity.Post;
import org.example.backend.entity.User;
import org.example.backend.enums.CommentStatus;
import org.example.backend.enums.PostStatus;
import org.example.backend.enums.StatusCode;
import org.example.backend.enums.UserStatus;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.mapper.PostMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    public boolean sendComment(Long replyId, Long postId, String text){
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        User user = userMapper.selectById(replyId);
        //用户和帖子校验
        if (userMe == null){
            throw new BusiException(StatusCode.USERNOEXIST);
        }
        if(replyId == null || replyId == 0){
            throw new BusiException(StatusCode.USERNOEXIST);
        }
        if(userMe.getStatus().equals(UserStatus.BLOCKED) || user.getStatus().equals(UserStatus.BLOCKED)){
            throw new BusiException(StatusCode.USERBLOCKED);
        }

        Post Ipost = postMapper.selectById(postId);//仅校验用
        if (Ipost == null){
            throw new BusiException(StatusCode.NOPOST);
        }
        if(Ipost.getPostStatus().equals(PostStatus.BLOCKED)){
            throw new BusiException(StatusCode.POSTBLOCKED);
        }
        //校验被回复用户是不是在评论区发言过或者是帖主
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCommenterId, replyId);
        //查有没有发过评论
        Comment replyComment = commentMapper.selectOne(wrapper);
        if (replyComment != null){//发过，就回复别人的评论
            if(replyComment.getCommentStatus().equals(CommentStatus.BLOCKED)){
                throw new BusiException(StatusCode.COMMENTBLOCKED);
            }//评论被锁禁止回复

            Long opPostId = replyComment.getPostId();//评论是不是同一个帖子下
            if(postId == null || opPostId == null) {//防黑客的
                throw new BusiException(StatusCode.NOPOST);
            }
            if (!postId.equals(opPostId)) {//不在一个帖子下
                throw new BusiException(StatusCode.INVALID, "回复的评论不属于该帖子");
            }
        } else {//回复帖主的，因为前面校验过帖子存不存在所以可以直接校验这个用户是不是发过帖子
            Long count = postMapper.selectCount(new LambdaQueryWrapper<Post>().eq(Post::getPosterId, replyId));
            if (count == 0) throw new BusiException(StatusCode.NOPOST);
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
