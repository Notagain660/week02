package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.entity.User;
import org.example.backend.enums.Role;
import org.example.backend.enums.StatusCode;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class AdminService {

    public final UserMapper userMapper;

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
}
