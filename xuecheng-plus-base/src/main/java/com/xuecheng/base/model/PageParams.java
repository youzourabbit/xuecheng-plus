package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 分页查询中的分页参数，请求模型类
 */
@Data
@ToString
public class PageParams {
    //设置默认值
    //当前页码
    @ApiModelProperty("页码")
    private Long pageNo = 1L; //声明此数据为long

    //每页记录数默认值
    @ApiModelProperty("每页记录数")
    private Long pageSize = 10L;

    public PageParams() {
    }

    public PageParams(long pageNo, long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
