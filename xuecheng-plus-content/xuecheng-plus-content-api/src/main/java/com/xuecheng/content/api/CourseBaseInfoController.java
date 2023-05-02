package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description 课程信息编辑接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口")//修改Swagger主页的英文，用汉字显示
@RestController
public class CourseBaseInfoController {
    @ApiOperation("课程查询接口") //告诉创建的文档，这个接口是“课程查询接口”
    @PostMapping("/course/list") //RequestMapping支持post、get、put......
    //这里就定义了一个接口
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams){
        return null;
    }

}
