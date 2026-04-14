package org.example.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Chat;
import org.example.backend.entity.Relation;
import org.example.backend.entity.User;
import org.example.backend.enums.ChatStatus;
import org.example.backend.enums.OurStatus;
import org.example.backend.enums.StatusCode;
import org.example.backend.enums.UserStatus;
import org.example.backend.mapper.ChatMapper;
import org.example.backend.mapper.RelationMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class ChatService {

    private final UserMapper userMapper;
    private final ChatMapper chatMapper;
    private final RelationMapper relationMapper;

    public boolean sendChat(long userId, String content) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        User user= userMapper.selectById(userId);
        if (userMe == null || user == null) {
            throw new BusiException(StatusCode.USERNOEXIST);
        }

        LambdaQueryWrapper<Relation> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Relation::getMyId, userMe.getUserId())
                        .eq(Relation::getItsId, userId))
                .or(w -> w.eq(Relation::getMyId, userId)
                        .eq(Relation::getItsId, userMe.getUserId()));
        Relation relation = relationMapper.selectOne(wrapper);

        if(relation == null || !relation.getOurStatus().equals(OurStatus.kNITOK)){
            throw  new BusiException(StatusCode.NORELATION);
        }

        if(userMe.getStatus().equals(UserStatus.BLOCKED) || user.getStatus() == UserStatus.BLOCKED){
            throw new BusiException(StatusCode.USERBLOCKED, "禁止发送消息");
        }
        if (Objects.equals(userMe.getUserId(), user.getUserId())) {
            throw new BusiException(StatusCode.NOSELFCHAT);
        }

        Chat chat = new Chat();
        chat.setSenderId(userMe.getUserId());
        chat.setReceiverId(user.getUserId());
        chat.setChatContent(content);
        chat.setSendTime(LocalDateTime.now());
        chat.setChatStatus(ChatStatus.NOTYET);
        return chatMapper.insert(chat) == 1;
    }

    public boolean readChat(long chatId) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        Chat chat = chatMapper.selectById(chatId);

        if(!Objects.equals(chat.getReceiverId(), userMe.getUserId()) ||
           !chat.getChatStatus().equals(ChatStatus.NOTYET)){//是不是用户能操作的消息
            throw new BusiException(StatusCode.NOTFOUND);
        }

        chat.setChatStatus(ChatStatus.READ);
        chat.setReadTime(LocalDateTime.now());
        return chatMapper.updateById(chat) == 1;
    }

    public boolean deleteChat(long chatId) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        Chat chat = chatMapper.selectById(chatId);

        if(!Objects.equals(chat.getReceiverId(), userMe.getUserId()) && !Objects.equals(chat.getSenderId(), userMe.getUserId())){
            throw new BusiException(StatusCode.NOTFOUND);
        }

        chat.setChatStatus(ChatStatus.MEDELETED);
        return chatMapper.updateById(chat) == 1;
    }

    public List<Chat> checkChats(Long userId) {
        User userMe = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        LambdaQueryWrapper<Chat> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Chat::getSenderId, userMe.getUserId())
                        .eq(Chat::getReceiverId, userId))
               .or(w -> w.eq(Chat::getReceiverId, userMe.getUserId())
                       .eq(Chat::getSenderId, userId));
        List<Chat> chats = chatMapper.selectList(wrapper);
        return chats.stream()
                .filter(chat -> {
                    if (Objects.equals(chat.getSenderId(), userMe.getUserId())) {
                        return chat.getChatStatus() != ChatStatus.MEDELETED;
                    } else {
                        return chat.getChatStatus() != ChatStatus.OPDELETED;
                    }
                })
                .sorted(Comparator.comparing(Chat::getSendTime))
                .collect(Collectors.toList());
    }
}
