package org.example.backend.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);


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

        post.setTopExpireTime(LocalDateTime.now().plusHours(24));

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

        post.setTopExpireTime(null);

        return postMapper.updateById(post) == 1;
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void clearExpiredTop() {
        UpdateWrapper<Post> wrapper = new UpdateWrapper<>();
        wrapper.set("pinornot", false)
               .set("top_expire_time", null)
               .eq("pinornot", true)
               .lt("top_expire_time", LocalDateTime.now());
        postMapper.update(null, wrapper);
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

    public Map<String, String> statistics(){
        List<Map<String, Object>> items = statis.selectItems();
        String clue = JSONUtil.toJsonStr(items);
        String AISummary;
        try {
            AISummary = aiService.generateStatistics(items);
            if (AISummary == null || AISummary.trim().isEmpty()) {
                AISummary = "AI 未返回有效总结。";
            }
        } catch (Exception e) {
            log.error("AI 调用失败", e);
            AISummary = "AI 服务暂时不可用。";
        }

        Map<String, String> result = new HashMap<>();
        result.put("OriginData", clue);
        result.put("AISummary", AISummary);
        return result;
    }

    public Map<String, String> statisticsPost(){
        List<Map<String, Object>> items = statis.selectPlaces();
        String clue = JSONUtil.toJsonStr(items);
        String AISummary;
        try {
            AISummary = aiService.generateStatisticsPlace(items);
            if (AISummary == null || AISummary.trim().isEmpty()) {
                AISummary = "AI 未返回有效总结。";
            }
        } catch (Exception e) {
            log.error("AI 调用失败", e);
            AISummary = "AI 服务暂时不可用。";
        }

        Map<String, String> result = new HashMap<>();
        result.put("OriginData", clue);
        result.put("AISummary", AISummary);
        return result;
    }

}
