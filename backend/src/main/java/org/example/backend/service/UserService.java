package org.example.backend.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.LoginReturnData;
import org.example.backend.entity.User;
import org.example.backend.enums.StatusCode;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Value("${jwt.secretkey}")
    private String secretkey;
    private final UserMapper userMapper;

    public boolean register(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserPhone, user.getUserPhone());
        if (userMapper.selectOne(wrapper) != null) {
            throw new BusiException(StatusCode.USEREXIST);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();//bcrypt校验
        String encodedPassword = encoder.encode(user.getPassword());//encode方法
        user.setPassword(encodedPassword);//存加密后的密码

        return userMapper.insert(user) == 1;
        //大于1可能校验是不是出现了漏洞因为单个用户注册不能一次插入多行数据
    }

    public LoginReturnData login(String account, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //不是成员变量是局部变量，否则wrapper会累积导致线程错误（？）
        wrapper.eq(User::getUserPhone, account)
               .or()
               .eq(User::getUserEmail, account);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusiException(StatusCode.USERNOEXIST);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            throw new BusiException(StatusCode.PASSWORDERROR);//match方法比对
        }
        //以下是用户存在并且密码正确才执行的操作，所以不用再判断了。
        Map<String, Object> map = new HashMap<>();
            map.put("issue_time", DateUtil.now());
            map.put("expire_time", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
            map.put("userId",  String.valueOf(user.getUserId()));
            map.put("user_email", user.getUserEmail());
            map.put("userphone", user.getUserPhone());
            map.put("role", user.getRole());

        String token =  JWTUtil.createToken(map, secretkey.getBytes(StandardCharsets.UTF_8));
        return new LoginReturnData(token, user);
    }

    public User checkme() {
        return userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
    }

    public boolean updateName(String userName){
        User user = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if (user == null) {
            throw new BusiException(StatusCode.USERNOEXIST);
        }
        user.setUserName(userName);
        return userMapper.updateById(user) == 1;
    }

    public boolean updateAvatar(String avatar) {
        User user = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        if (user == null) {
            throw new BusiException(StatusCode.USERNOEXIST);
        }
        user.setUserAvatar(avatar);
        return userMapper.updateById(user) == 1;
    }

}

