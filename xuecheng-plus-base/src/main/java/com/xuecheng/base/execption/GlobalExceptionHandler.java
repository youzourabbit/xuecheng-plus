package com.xuecheng.base.execption;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 全局异常处理器
 */
@Slf4j
@ControllerAdvice
// @RestControllerAdvice的功能相当于@ResponseBody+@ControllerAdvice
public class GlobalExceptionHandler {

    // 项目自定义异常处理器

    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e) {
        //使用log日志记录异常
        log.error("【系统异常】{}", e.getErrMessage(), e);
        String errMessage = e.getErrMessage();
        int errCode = e.getErrCode();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        if (errCode != 0) {
            RestErrorResponse restErrorResponse_errCode = new RestErrorResponse(errCode, errMessage);
            return restErrorResponse_errCode;
        }
        return restErrorResponse;

    }

    //解析多条错误（JSR303参数合法性校验）————将自定义的注解错误拦截,一次提交校验，就可能截获多条数据
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 最终数据格式是比较难直接使用的格式，所以需要进行解析，将其用列表进行返回
        BindingResult bindingResult = e.getBindingResult();
        List<String> errors = new ArrayList<>(); // 错误列表
        bindingResult.getFieldErrors().stream().forEach(item -> {
            errors.add(item.getDefaultMessage());
        });

        // 将list中的错误信息进行拼接
        String errMessage = StringUtils.join(errors, ",");// 这里使用逗号分割来拼接
        log.error("系统异常{}", e.getMessage(), e);

        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }
    /* // emmm什么时候写过了....
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        //使用log日志记录异常
        log.error("【系统异常】{}", e.getMessage(), e);
        String errMessage = e.getMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;

    }*/

    //如果自定义异常没包含遇到的异常，则转用本方法，展示本异常未知
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        log.error("【系统异常】{}", e.getMessage(), e);
        return new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());
    }
}
