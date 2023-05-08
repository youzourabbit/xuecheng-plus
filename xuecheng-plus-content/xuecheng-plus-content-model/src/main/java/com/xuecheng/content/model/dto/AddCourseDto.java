package com.xuecheng.content.model.dto;


import com.xuecheng.base.execption.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/* 尝试创建一个模型类，功能：添加课程的dto（数据传输对象）*/
@Data
@ApiModel(value = "AddCourse",description = "新增课程基本信息")
public class AddCourseDto {


    /* 使用这个注解可以方便进行报错的操作，但若是想要用一个方法处理不同形式的请求（例如get、post、delete，他们对数据排查肯定是会有点差异的）
    * 那么就不得不提到分组。
    * 创建一个新的类，类中将每一个组都定义成方法，不需要定义任何内容，直接用来分组 */

    @NotEmpty(message = "新增课程名称不能为空",groups = {ValidationGroups.Insert.class})
    @NotEmpty(message = "删除课程名称不能为空",groups = {ValidationGroups.Delete.class})
    @NotEmpty(message = "查询课程名称不能为空",groups = {ValidationGroups.Select.class})
    @NotEmpty(message = "修改课程名称不能为空",groups = {ValidationGroups.Update.class})
    @ApiModelProperty(value = "课程名称",required = true)
    private String name;

    //int i = 1/0;

    @NotEmpty(message = "适用人群不能为空")
    @ApiModelProperty(value = "适用人群",required = true)
    private String users;

    @ApiModelProperty(value = "课程标签")
    private String tags;

    @NotEmpty(message = "课程分类不能为空")
    @ApiModelProperty(value = "大分类",required = true)
    private String mt;

    @NotEmpty(message = "课程分类不能为空")
    @ApiModelProperty(value = "小分类",required = true)
    private String st;

    @NotEmpty(message = "课程等级不能为空")
    @ApiModelProperty(value = "课程等级",required = true)
    private String grade;

    @ApiModelProperty(value = "教学模式（普通、录播、直播等）",required = true)
    private String teachmode;

    @ApiModelProperty(value = "课程介绍")
    @Size(message = "课程描述内容过少，需要大于10个字",min = 10)
    private String description;

    @ApiModelProperty(value = "课程图片",required = true)
    private String pic;

    @NotEmpty(message = "收费规则不能为空")
    @ApiModelProperty(value = "收费规则，对应数据字典",required = true)
    private String charge;

    @ApiModelProperty(value = "价格")
    private Float price;
    @ApiModelProperty(value = "原价")
    private Float originalPrice;

    @ApiModelProperty(value = "qq")
    private String qq;

    @ApiModelProperty(value = "微信")
    private String wechat;
    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "有效期")
    private Integer validDays;
}
