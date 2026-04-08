package org.example.lostnfind.service;

import lombok.RequiredArgsConstructor;
import org.example.lostnfind.entity.User;
import org.example.lostnfind.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class UserService {

    private final UserMapper userMapper;

    public boolean register(User user) {
        return userMapper.insert(user) == 1;
    }


}
