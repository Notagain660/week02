package org.example.lostnfind.enums;

import lombok.Getter;

@Getter
public enum StatusCode {
    OK(200, "操作成功"),
    CREATED(201, "创建资源成功"),

    INVALID(400, "无效请求语法"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限访问"),
    NOTFOUND(404, "找不到资源"),
    COLLAPSED(500, "服务器不知道怎么处理"),

    USEREXIST(1001, "用户已存在"),
    USERNOEXIST(1002, "用户不存在"),
    PASSWORDERROR(1003, "密码错误"),

    NORELATION(2000, "你与ta还不是好友"),
    NOSELFRELA(2001, "不能添加自己为好友"),

    NOPOST(3000, "帖子不存在"),
    POSTBLOCKED(3001, "帖子已封禁"),
    NOCOMMENT(3002, "评论不存在"),
    COMMENTBLOCKED(3003, "评论已封禁"),

    DBERROR(5001, "数据库错误");
    private final int code;
    private final String message;
    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
