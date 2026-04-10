package org.example.backend.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.LoginReturnData;
import org.example.backend.entity.User;
import org.example.backend.enums.StatusCode;
import org.example.backend.service.UserService;
import org.example.backend.utilities.MapperResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

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



}
