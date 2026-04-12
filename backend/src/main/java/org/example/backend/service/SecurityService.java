package org.example.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.SecurityDTO;
import org.example.backend.entity.User;
import org.example.backend.enums.StatusCode;
import org.example.backend.mapper.UserMapper;
import org.example.backend.tokener.ThreadContext;
import org.example.backend.utilities.BusiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class SecurityService {
    private final UserMapper userMapper;

    public boolean updatePassword(SecurityDTO DTO) {
        User user = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();//旧密码校验
        if(!encoder.matches(DTO.getPasserword(), user.getPassword())) {
            throw new BusiException(StatusCode.PASSWORDERROR);
        }

        if(!DTO.getPhoner().equals(user.getUserPhone())){
            throw new BusiException(StatusCode.INVALID, "手机号验证失败");
        }
        if(!DTO.getEmailer().equals(user.getUserEmail())){
            throw new BusiException(StatusCode.INVALID, "邮箱验证失败");
        }


        if (DTO.getStr() != null && !DTO.getStr().isEmpty()) {
            user.setPassword(encoder.encode(DTO.getStr()));
        }
        return userMapper.updateById(user) == 1;
    }

    public boolean updatePhone(SecurityDTO DTO) {
        User user = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(DTO.getPasserword(), user.getPassword())) {
            throw new BusiException(StatusCode.PASSWORDERROR);
        }

        if(!DTO.getPhoner().equals(user.getUserPhone())){
            throw new BusiException(StatusCode.INVALID, "手机号验证失败");
        }
        if(!DTO.getEmailer().equals(user.getUserEmail())){
            throw new BusiException(StatusCode.INVALID, "邮箱验证失败");
        }

        if(DTO.getStr() != null && !DTO.getStr().isEmpty()) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUserPhone, DTO.getStr());
            User exist = userMapper.selectOne(wrapper);
            if(exist != null && !Objects.equals(exist.getUserId(), user.getUserId())) {
                throw new BusiException(StatusCode.USEREXIST, "手机号已被占用");
            }
            user.setUserPhone(DTO.getStr());
        }
        return userMapper.updateById(user) == 1;
    }

    public boolean updateEmail(SecurityDTO DTO) {
        User user = userMapper.selectById(ThreadContext.getCurrentUser().getUserId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(DTO.getPasserword(), user.getPassword())) {
            throw new BusiException(StatusCode.PASSWORDERROR);
        }
        if(!DTO.getPhoner().equals(user.getUserPhone())){
            throw new BusiException(StatusCode.INVALID, "手机号验证失败");
        }
        if(!DTO.getEmailer().equals(user.getUserEmail())){
            throw new BusiException(StatusCode.INVALID, "邮箱验证失败");
        }
        if (DTO.getStr() != null && !DTO.getStr().isEmpty()) {
            user.setUserEmail(encoder.encode(DTO.getStr()));
        }
        return userMapper.updateById(user) == 1;
    }
}
