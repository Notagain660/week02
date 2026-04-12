package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Comment;
import org.example.backend.entity.User;
import org.example.backend.enums.CommentStatus;
import org.example.backend.enums.StatusCode;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    public boolean sendComment(Long replyId, Long postId, String text){
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if (userMe == null){
            throw new BusiException(StatusCode.USERNOEXIST);
        }
        Long opPostId = commentMapper.selectById(replyId).getPostId();
        if(postId == null || opPostId == null) {
            throw new BusiException(StatusCode.NOPOST);
        }
        if(!postId.equals(opPostId)){//保证回复和被回复的评论在同一帖子下
            throw new BusiException(StatusCode.INVALID);
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
