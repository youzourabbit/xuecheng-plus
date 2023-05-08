package com.xuecheng.base.execption;

import java.io.Serializable;

// 抛出的错误相应参数的包装
// 作用：和前端约定，返回的异常信息的模型
public class RestErrorResponse implements Serializable {
    private String errMessage;// 约定：返回的参数键为errMessage

    public RestErrorResponse(String errMessage){
        this.errMessage = errMessage;
    }

    public String getErrMessage(){
        return errMessage;
    }

    public void setErrMessage(String errMessage){
        this.errMessage = errMessage;
    }
}
