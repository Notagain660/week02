package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Comment;
import org.example.backend.entity.Post;
import org.example.backend.entity.Report;
import org.example.backend.entity.User;
import org.example.backend.enums.*;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.mapper.PostMapper;
import org.example.backend.mapper.ReportMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class ReportService {
    private final UserMapper userMapper;
    private final ReportMapper reportMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public List<Report> getReports() {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if(userMe == null)
            throw new BusiException(StatusCode.USERNOEXIST);
        if(!userMe.getRole().equals(Role.ADMIN))
            throw new BusiException(StatusCode.INVALID);
        return reportMapper.selectList(null);
    }

    public boolean report(Report report) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        User user= userMapper.selectById(report.getReportee());
        if (userMe == null || user == null)
            throw new BusiException(StatusCode.USERNOEXIST);
        report.setReporter(userMe.getUserId());
        report.setStatus(ReportStatus.NOT);//前端不提供状态接口
        return reportMapper.insert(report) == 1;
    }

    public boolean dealReport(Long reportId, Integer status) {
        Report report = reportMapper.selectById(reportId);
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if(userMe == null)
            throw new BusiException(StatusCode.USERNOEXIST);
        if(!userMe.getRole().equals(Role.ADMIN))
            throw new BusiException(StatusCode.INVALID);
        report.setStatus(ReportStatus.fromValue(status));
        return reportMapper.updateById(report) == 1;
    }

    public boolean block (Integer type, Long contentId){
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if(userMe == null)
            throw new BusiException(StatusCode.USERNOEXIST);
        if(!userMe.getRole().equals(Role.ADMIN))
            throw new BusiException(StatusCode.INVALID);

        ReportType reportType = ReportType.fromValue(type);
        if(reportType == null)
            throw new BusiException(StatusCode.INVALID);
        switch (reportType) {
            case USER -> {
                User user = userMapper.selectById(contentId);
                if(user == null)
                    throw new BusiException(StatusCode.USERNOEXIST);
                user.setStatus(UserStatus.BLOCKED);
                return userMapper.updateById(user) == 1;
            }
            case POST -> {
                Post post = postMapper.selectById(contentId);
                if(post == null)
                    throw new BusiException(StatusCode.NOPOST);
                post.setPostStatus(PostStatus.BLOCKED);
                return postMapper.updateById(post) == 1;
            }
            case COMMENT -> {
                Comment comment = commentMapper.selectById(contentId);
                if(comment == null)
                    throw new BusiException(StatusCode.NOCOMMENT);
                comment.setCommentStatus(CommentStatus.BLOCKED);
                return commentMapper.updateById(comment) == 1;
            }
        }
        return false;
    }


}
