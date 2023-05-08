package com.xuecheng.base.execption;

// 自定义异常类，带有自定义的属性。此后抛出异常就是自己的类型
public class XueChengPlusException extends RuntimeException{
    private String errMessage;
    public XueChengPlusException(){
        super();
    }
    public XueChengPlusException(String errMessage){
        super(errMessage);
        this.errMessage = errMessage;

    }
    public String getErrMessage(){
        return errMessage;
    }

    /*cast的重载方法，通过cast获取一些通用方法*/
    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }
}
