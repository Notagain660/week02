package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Comment;
import org.example.backend.entity.Post;
import org.example.backend.entity.User;
import org.example.backend.enums.*;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.mapper.PostMapper;
import org.example.backend.mapper.StatisticsMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class AdminService {

    public final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final StatisticsMapper statis;
    private final AIService aiService;

    public User adminCheck(Long id) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if(userMe == null)
            throw new BusiException(StatusCode.USERNOEXIST);
        if(!userMe.getRole().equals(Role.ADMIN))
            throw new BusiException(StatusCode.INVALID);

        User user = userMapper.selectById(id);
        if(user == null)
            throw new BusiException(StatusCode.USERNOEXIST);
        return user;
    }

    public boolean pinOrNot(Long id) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if(userMe == null)
            throw new BusiException(StatusCode.USERNOEXIST);
        if(!userMe.getRole().equals(Role.ADMIN))
            throw new BusiException(StatusCode.INVALID);

        Post post = postMapper.selectById(id);
        if(post == null)
            throw new BusiException(StatusCode.NOPOST);
        post.setPinOrNot(true);
        return postMapper.updateById(post) == 1;
    }

    public boolean releasePin(Long id) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if(userMe == null)
            throw new BusiException(StatusCode.USERNOEXIST);
        if(!userMe.getRole().equals(Role.ADMIN))
            throw new BusiException(StatusCode.INVALID);
        Post post = postMapper.selectById(id);
        if(post == null)
            throw new BusiException(StatusCode.NOPOST);
        post.setPinOrNot(false);
        return postMapper.updateById(post) == 1;
    }

    public boolean delete(Long id, Integer type) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if(userMe == null)
            throw new BusiException(StatusCode.USERNOEXIST);
        if(!userMe.getRole().equals(Role.ADMIN))
            throw new BusiException(StatusCode.INVALID);

        ReportType reportType = ReportType.fromValue(type);
        if(reportType == null || reportType.equals(ReportType.USER))
            throw new BusiException(StatusCode.INVALID);

        switch(reportType) {
            case POST -> {
                Post post = postMapper.selectById(id);
                if(post == null)
                    throw new BusiException(StatusCode.NOPOST);
                post.setPostStatus(PostStatus.DELETED);
                return postMapper.updateById(post) == 1;
            }
            case COMMENT -> {
                Comment comment = commentMapper.selectById(id);
                if(comment == null)
                    throw new BusiException(StatusCode.NOCOMMENT);
                comment.setCommentStatus(CommentStatus.DELETED);
                return commentMapper.updateById(comment) == 1;
            }
        }
        return false;
    }

    public Integer selectActive(LocalDate start, LocalDate end) {
        LocalDateTime start1 = start.atStartOfDay();
        LocalDateTime end1 = end.plusDays(1).atStartOfDay().minusNanos(1);
        List<Long> activeIds = statis.selectActive(start1, end1);

        return activeIds.size();
    }

    public Integer selectPost(){
        return statis.selectPosts();
    }

    public Integer selectFound(){
        return statis.selectFound();
    }

    public String statistics(){
        List<Map<String, Object>> items = statis.selectItems();
        String AIOutput = aiService.generateStatistics(items);
        int start = AIOutput.indexOf("{");
        int end = AIOutput.indexOf("}") + 1;
        return AIOutput.substring(start, end);
    }

    public String statisticsPost(){
        List<Map<String, Object>> items = statis.selectPlaces();
        String AIOutput = aiService.generateStatisticsPlace(items);
        int start = AIOutput.indexOf("{");
        int end = AIOutput.indexOf("}") + 1;
        return AIOutput.substring(start, end);
    }

}
