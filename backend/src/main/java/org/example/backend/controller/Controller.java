package org.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.*;
import org.example.backend.enums.OurStatus;
import org.example.backend.enums.StatusCode;
import org.example.backend.service.*;
import org.example.backend.utilities.MapperResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    private final RelationService relationService;
    private final ChatService chatService;
    private final PostService postService;
    private final ChatModel chatModel;
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private final CommentService commentService;
    private final ReportService reportService;
    private final AdminService adminService;


    @GetMapping//直接输入网址就能测试是否通了
    public String trial(){
        log.info("Ciallo");
        return "Hello World";
    }

    @GetMapping("/test/ai")
    public String testAI() {
        return chatModel.call(new Prompt("用中文简单介绍一下人工智能"))
                .getResult().getOutput().getText();
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

    @GetMapping("/user/checkme")
    public MapperResult<User> checkme(){
        User user = userService.checkme();
        return MapperResult.success(StatusCode.OK, user);
    }

    @PutMapping("/user/change/name")
    public MapperResult<Object> changeName(@RequestParam String name) {
        boolean result = userService.updateName(name);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/user/change/avatar")
    public MapperResult<Object> changeAvatar(@RequestPart ("image") MultipartFile image) {
        //文件上传需要发送二进制数据，不能用 JSON 字符串表示文件名
        //这个是可被访问的url
        boolean result = userService.updateAvatar(userService.getAvatar(image));
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        }  else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/user/change/password")
    public MapperResult<Object> changePassword(@RequestBody SecurityDTO DTO){
        boolean result = securityService.updatePassword(DTO);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID, "修改密码失败");
        }
    }

    @PutMapping("/user/change/phone")
    public MapperResult<Object> changePhone(@RequestBody SecurityDTO DTO){
        boolean result = securityService.updatePhone(DTO);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID, "修改手机号失败");
        }
    }

    @PutMapping("/user/change/email")
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

    @GetMapping("/relation/{ourStatus}")
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

    @PostMapping("/relation/request")
    public MapperResult<Object> request(@RequestParam Long userId){
        boolean result = relationService.request(userId);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/relation/{userId}/{ourStatus}/{act}")
    public MapperResult<Object> agree(@PathVariable Long userId, @PathVariable int ourStatus, @PathVariable int act){
        boolean result = relationService.dealRelation(userId, OurStatus.fromValue(ourStatus)
                        , act);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PostMapping("/chat/{userId}")
    public MapperResult<Object> sendChat(@PathVariable Long userId, @RequestParam String content){
        boolean result = chatService.sendChat(userId, content);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        }  else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @GetMapping("/chat/check")
    public MapperResult<List<Chat>> checkChat(@RequestParam Long userId){
        List<Chat> chats = chatService.checkChats(userId);
        return MapperResult.success(StatusCode.OK, chats);
    }

    @PutMapping("/chat/read/{chatId}")
    public MapperResult<Object> readChat(@PathVariable Long chatId){
        boolean result = chatService.readChat(chatId);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else  {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/chat/delete/{chatId}")
    public MapperResult<Object> deleteChat(@PathVariable Long chatId){
        boolean result = chatService.deleteChat(chatId);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else  {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PostMapping("/post/image")
    public MapperResult<String> postImage(@RequestPart (value = "image", required = false) MultipartFile image){
        String postImage = postService.getImage(image);
        return MapperResult.success(StatusCode.OK, postImage);
    }

    @PostMapping("/post/send")
    public MapperResult<Post> post(@RequestBody Post post){
        boolean result = postService.post(post);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        }  else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @GetMapping("/post")
    public MapperResult<IPage<PostDTO>> listPosts(
            @RequestParam(defaultValue = "1") int pageCode,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,//可选筛选和模糊搜索
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String itemPlace) {
        IPage<PostDTO> pageResult = postService.browsePost(pageCode, size, type, status, itemName, itemPlace);
        return MapperResult.success(StatusCode.OK, pageResult);
    }

    @PutMapping("/post/update")
    public MapperResult<Post>  updatePost(@RequestBody Post post){
        boolean result = postService.updatePost(post);
        if (result) {//既然这样那用户删除功能也可以调用这个就是前端只能展示一个选项。
            return MapperResult.success(StatusCode.OK, null);
        } else {//类比QQ说说的更新功能，把没改的字段保持原样和改了的一起上传
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @GetMapping("/post/{postId}")
    public MapperResult<Post> checkPost(@PathVariable Long postId){
        Post post = postService.checkPost(postId);
        return MapperResult.success(StatusCode.OK, post);
    }

    @PutMapping("/post/delete/{postId}")
    public MapperResult<Object> deletePost(@PathVariable Long postId){
        boolean result = postService.deletePost(postId);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PostMapping("/comment/{postId}/{replyId}")
    public MapperResult<Object> postReply(@PathVariable Long postId
            , @PathVariable Long replyId, @RequestParam String text){
        boolean result = commentService.sendComment(replyId, postId, text);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        }  else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/comment/{commentId}")
    public MapperResult<Object> deleteComment(@PathVariable Long commentId){
        boolean result = commentService.deleteComment(commentId);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @GetMapping("/comment/list/{postId}")
    public MapperResult<IPage<CommentVO>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<CommentVO> pageResult = commentService.browsComment(postId, page, size);
        return MapperResult.success(StatusCode.OK, pageResult);}

    @PostMapping("/report")
    public MapperResult<Object> report(@RequestBody Report report){
        boolean result = reportService.report(report);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/report/deal/{reportId}")
    public MapperResult<Object> dealReport(@PathVariable Long reportId, @RequestParam Integer status){
        boolean result = reportService.dealReport(reportId, status);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @GetMapping("/report/check")
    public MapperResult<List<Report>> checkReport(){
        return  MapperResult.success(StatusCode.OK, reportService.getReports());
    }

    @PutMapping("/report/{type}/{contentid}")
    public MapperResult<Object> block(@PathVariable Integer type, @PathVariable Long contentid){
        boolean result = reportService.block(type, contentid);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @GetMapping("/user/check/{userId}")
    public MapperResult<User> checkUser(@PathVariable Long userId){
        User user = adminService.adminCheck(userId);
        return MapperResult.success(StatusCode.OK, user);
    }

    @PutMapping("/admin/pin/{postId}")
    public MapperResult<Object> pin(@PathVariable Long postId){
        boolean result = adminService.pinOrNot(postId);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/admin/relasepin/{postId}")
    public MapperResult<Object> releasePin(@PathVariable Long postId){
        boolean result = adminService.releasePin(postId);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @PutMapping("/admin/delete/{type}/{id}")
    public MapperResult<Object> deleteAdmin(@PathVariable Integer type, @PathVariable Long id){
        boolean result = adminService.delete(id, type);
        if (result) {
            return MapperResult.success(StatusCode.OK, null);
        } else {
            return MapperResult.error(StatusCode.INVALID);
        }
    }

    @GetMapping("/admin/statistics/active")
    public MapperResult<Integer> activeAdmin(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        return MapperResult.success(StatusCode.OK, adminService.selectActive(start, end));
    }

    @GetMapping("/admin/statistics/post")
    public MapperResult<Integer> totalPosts(){
        return MapperResult.success(StatusCode.OK, adminService.selectPost());
    }

    @GetMapping("/admin/statistics/found")
    public MapperResult<Integer> totalFound(){
        return MapperResult.success(StatusCode.OK, adminService.selectFound());
    }

    @GetMapping("/admin/statistics/item")
    public MapperResult<Map<String, String>> item(){
        return MapperResult.success(StatusCode.OK, adminService.statistics());
    }

    @GetMapping("/admin/statistics/place")
    public MapperResult<Map<String, String>> place(){
        return MapperResult.success(StatusCode.OK, adminService.statisticsPost());
    }
}
