package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.web.bind.annotation.RequestBody;

public interface CourseBaseInfoService {
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);


    CourseBaseInfoDto createCourseBase(/*将来机构ID不会给客户端定义*/Long companyId,@RequestBody AddCourseDto addCourseDto);

    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    //CourseBaseInfoDto selectCourseBase(String id);

    /**
     *
     * @param companyId 修改课程依然要把companyId单独处理
     * @param editCourseDto 修改课程的基本信息载体
     * @return 课程基本信息
     */
    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

}