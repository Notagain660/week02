package org.example.backend.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.*;
import org.example.backend.enums.OurStatus;
import org.example.backend.enums.StatusCode;
import org.example.backend.service.ChatService;
import org.example.backend.service.RelationService;
import org.example.backend.service.SecurityService;
import org.example.backend.service.UserService;
import org.example.backend.utilities.MapperResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "参数错误")
})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class Controller {

    private final UserService userService;
    private final SecurityService securityService;

    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private final RelationService relationService;
    private final ChatService chatService;

    @GetMapping//直接输入网址就能测试是否通了
    public String trial(){
        log.info("Ciallo");
        return "Hello World";
    }

    @PostMapping("/register")
    public MapperResult<Object> register(@Valid @RequestBody User user) {
        boolean result = userService.register(user);
        if (result) {
            return MapperResult.success(StatusCode.CREATED, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PostMapping("/login")
    public MapperResult<Object> login(@Valid @RequestBody Map<String, String> loginInfo) {
        String account = loginInfo.get("account");
        String password = loginInfo.get("password");
        LoginReturnData loginReturnData = userService.login(account, password);
        return MapperResult.success(StatusCode.OK, loginReturnData);
    }

    @GetMapping("/checkme")
    public MapperResult<User> checkme(){
        User user = userService.checkme();
        return MapperResult.success(StatusCode.OK, user);
    }

    @PutMapping("/change/name")
    public MapperResult<Object> changeName(@RequestParam String name) {
        boolean result = userService.updateName(name);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/change/avatar")
    public MapperResult<Object> changeAvatar(@RequestPart ("image") MultipartFile image) {
        //文件上传需要发送二进制数据，不能用 JSON 字符串表示文件名
        if (image == null || image.isEmpty()) {//空图片校验
            return MapperResult.error(StatusCode.INVALID, "请选择图片");
        }

        String originalName = image.getOriginalFilename();//图片原始文件名（有路径）
        String extendName;
        if (originalName != null ) {//防止空指针错误
            if(originalName.contains(".")) {//文件名和扩展名校验
                extendName = originalName.substring(originalName.lastIndexOf("."));//取扩展名
                List<String> allowedExtends = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
                if (!allowedExtends.contains(extendName.toLowerCase())) {
                    return MapperResult.error(StatusCode.INVALID, "不支持的图片格式");
                }
            } else {//默认
                extendName = ".jpg";
            }
        } else {
            return MapperResult.error(StatusCode.NOTFOUND, "获取图片资源失败");
        }

        String fileName = UUID.randomUUID() + extendName;//生成安全的文件名
        String uploadD = "D:/uploads/avatar/";//准备目录
        File file = new File(uploadD + fileName);
        //保存（本地d盘已经建了目录所以没必要再判断和创建目录了）
        try {
            image.transferTo(file);//上传的临时文件直接写入到指定的目标文件，目录不存在爆IOException
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return MapperResult.error(StatusCode.DBERROR, "图片保存失败");
        }

        String avatar = "/avatar/" + fileName;//这个是可被访问的url
        boolean result = userService.updateAvatar(avatar);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        }  else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/change/password")
    public MapperResult<Object> changePassword(@RequestBody SecurityDTO DTO){
        boolean result = securityService.updatePassword(DTO);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID, "修改密码失败");
        }
    }

    @PutMapping("/change/phone")
    public MapperResult<Object> changePhone(@RequestBody SecurityDTO DTO){
        boolean result = securityService.updatePhone(DTO);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID, "修改手机号失败");
        }
    }

    @PutMapping("/change/email")
    public MapperResult<Object> changeEmail(@RequestBody SecurityDTO DTO){
        boolean result = securityService.updateEmail(DTO);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        }  else {
            return MapperResult.error(StatusCode.INVALID, "修改邮箱失败");
        }
    }

    @GetMapping("/user/{userId}")
    public MapperResult<OtherInfo> getOtherProfile(@PathVariable Long userId){
        OtherInfo oth = relationService.getOtherProfile(userId);
        return MapperResult.success(StatusCode.OK, oth);
    }

    @GetMapping("/user/{ourStatus}")
    public MapperResult<List<Relation>> getUserProfile(@PathVariable int ourStatus){
        if(ourStatus != 1 && ourStatus != 2){
            return MapperResult.error(StatusCode.INVALID);
        }
        OurStatus status = OurStatus.fromValue(ourStatus);
        List<Relation> list;
        if (status != null) {
            list = relationService.getRelations(status);
        } else {
            return MapperResult.error(StatusCode.NOTFOUND);
        }
        return MapperResult.success(StatusCode.OK, list);
    }

    @PostMapping("/user/request")
    public MapperResult<Object> request(@RequestParam Long userId){
        boolean result = relationService.request(userId);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PostMapping("/user/relation/{userId}/{ourStatus}/{act}")
    public MapperResult<Object> agree(@PathVariable Long userId, @PathVariable int ourStatus, @PathVariable int act){
        boolean result = relationService.dealRelation(userId, OurStatus.fromValue(ourStatus)
                        , act);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PostMapping("/user/chat/{userId}")
    public MapperResult<Object> sendChat(@PathVariable Long userId, @RequestParam String content){
        boolean result = chatService.sendChat(userId, content);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        }  else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @GetMapping("/user/chat/check")
    public MapperResult<List<Chat>> checkChat(@RequestParam Long userId){
        List<Chat> chats = chatService.checkChats(userId);
        return MapperResult.success(StatusCode.OK, chats);
    }

    @PutMapping("/user/chat/read/{chatId}")
    public MapperResult<Object> readChat(@PathVariable Long chatId){
        boolean result = chatService.readChat(chatId);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else  {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/user/chat/delete/{chatId}")
    public MapperResult<Object> deleteChat(@PathVariable Long chatId){
        boolean result = chatService.deleteChat(chatId);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else  {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

}
