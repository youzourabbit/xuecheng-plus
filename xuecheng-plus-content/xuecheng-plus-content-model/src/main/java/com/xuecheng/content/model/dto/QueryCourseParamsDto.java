package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @description 课程查询参数Dto，Dto是面向服务、微服务的,DTO就是数据传输对，DTO需要数据就访问PO；而po近似DAO，面向数据库（因为po类太多，就一起写在这了）
 * 另外有VO，是面向微服务的，同时可以取代Dto，去应对访问对象不固定的业务，处理后交给Dto。
 * （前段服务主要有：Vue，React
 * @author Mr.M
 * @date 2022/9/6 14:36
 * @version 1.0
 */
@Data
@ToString
public class QueryCourseParamsDto {

    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;

}
