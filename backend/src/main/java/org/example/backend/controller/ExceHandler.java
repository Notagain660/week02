package org.example.backend.controller;

import cn.hutool.core.io.resource.NoResourceException;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.enums.StatusCode;
import org.example.backend.utilities.BusiException;
import org.example.backend.utilities.MapperResult;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;


@RestControllerAdvice//处理controller抛出的异常，返回为json，Spring启动时会自动扫描并注册这个 Bean
@Slf4j
public class ExceHandler {
    @ExceptionHandler(BusiException.class)
    public MapperResult<Void> handleBusiException(BusiException e) {
        log.warn(e.getMessage());
        return MapperResult.error(e.getStatusCode(), null);
    }

    @ExceptionHandler(NoResourceException.class)
    public MapperResult<Void> handleNoResourceException(NoResourceException e) {
        log.error(e.getMessage());
        return MapperResult.error(StatusCode.NOTFOUND, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public MapperResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        //Spring 中用于存放数据绑定和参数校验结果的对象。当你在 Controller 的方法参数上使用 @Valid 或 @Validated 注解时
        //Spring 会把校验过程中产生的所有错误（如字段为空、格式不对、长度超限等）收集到 BindingResult 中。
        String message = bindingResult.getAllErrors().getFirst().getDefaultMessage();//取第一条
        return MapperResult.error(StatusCode.INVALID, message);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public MapperResult<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage());
        return MapperResult.error(StatusCode.USEREXIST);
    }

    @ExceptionHandler(IOException.class)
    public MapperResult<Void> handleIOException(IOException e) {
        log.error(e.getMessage());
        return MapperResult.error(StatusCode.NOTFOUND);
    }

    @ExceptionHandler(Exception.class)//兜底用的
    public MapperResult<Void> handleException(Exception e) {
        log.error(e.getMessage());
        return MapperResult.error(StatusCode.COLLAPSED);
    }
}

