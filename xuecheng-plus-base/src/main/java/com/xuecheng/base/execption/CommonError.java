package com.xuecheng.base.execption;

/*enum:枚举类型，存放了通用的一些异常类型*/
// 定义一些常用的异常信息
public enum CommonError {

    UNKNOWN_ERROR("执行过程异常，请重试"),
    PARAMS_ERROR("非法参数"),
    OBJECT_NULL("对象为空"),
    QUERY_NULL("查询结果为空"),
    REQUEST_NULL("请求参数为空"),
    TEACHPLAN_TREE_NODE_EXIT(120409,"课程计划信息还有子级信息，无法操作");

    private int errCode;
    private String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    private CommonError( String errMessage) {
        this.errMessage = errMessage;
    }

    private CommonError(int errCode,String errMessage){
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

}
