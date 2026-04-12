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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Value("${jwt.secretkey}")
    private String secretkey;
    private final UserMapper userMapper;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

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

    public String getAvatar(MultipartFile image) {
        if (image == null || image.isEmpty()) {//空图片校验
            throw new BusiException(StatusCode.INVALID, "请选择图片");
        }

        String extendName = getString(image);

        String fileName = UUID.randomUUID() + extendName;//生成安全的文件名
        String uploadD = "D:/uploads/avatar/";//准备目录
        File file = new File(uploadD + fileName);
        //保存（本地d盘已经建了目录所以没必要再判断和创建目录了）
        try {
            image.transferTo(file);//上传的临时文件直接写入到指定的目标文件，目录不存在爆IOException
        } catch (IOException e) {
            log.error("头像上传失败", e);
            throw new BusiException(StatusCode.DBERROR, "图片保存失败");
        }
        
        return "/avatar/" + fileName;
    }

    public String getString(MultipartFile image) {//如果多个地方要用到图片就要把这个单独包装进utilities
        String originalName = image.getOriginalFilename();//图片原始文件名（有路径）
        String extendName;
        if (originalName != null ) {//防止空指针错误
            if(originalName.contains(".")) {//文件名和扩展名校验
                extendName = originalName.substring(originalName.lastIndexOf("."));//取扩展名
                List<String> allowedExtends = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
                if (!allowedExtends.contains(extendName.toLowerCase())) {
                    throw new BusiException(StatusCode.INVALID, "不支持的图片格式");
                }
            } else {//默认
                extendName = ".jpg";
            }
        } else {
            throw new BusiException(StatusCode.NOTFOUND, "获取图片资源失败");
        }
        return extendName;
    }

}

