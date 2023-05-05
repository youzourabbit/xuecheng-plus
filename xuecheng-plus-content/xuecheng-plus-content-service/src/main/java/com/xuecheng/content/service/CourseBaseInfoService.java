package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.web.bind.annotation.RequestBody;

public interface CourseBaseInfoService {
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);


    CourseBaseInfoDto createCourseBase(/*将来机构ID不会给客户端定义*/Long companyId,@RequestBody AddCourseDto addCourseDto);

}