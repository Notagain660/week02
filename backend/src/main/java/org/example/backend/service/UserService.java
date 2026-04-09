package org.example.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.User;
import org.example.backend.enums.StatusCode;
import org.example.backend.mapper.UserMapper;
import org.example.backend.utilities.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class UserService {

    private final UserMapper userMapper;
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

    public boolean register(User user) {
        wrapper.eq(User::getUserPhone, user.getUserPhone());
        if (userMapper.selectOne(wrapper) != null) {
            throw new BusiException(StatusCode.USEREXIST);
        }
        return userMapper.insert(user) == 1;//大于1可能校验是不是出现了漏洞因为单个用户注册不能一次插入多行数据
    }


}

