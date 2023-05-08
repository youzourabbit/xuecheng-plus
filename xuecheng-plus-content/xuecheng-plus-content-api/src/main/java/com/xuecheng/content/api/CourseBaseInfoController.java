package com.xuecheng.content.api;

import com.xuecheng.base.execption.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(value = "课程信息编辑接口", tags = "课程信息编辑接口")//修改Swagger主页的英文，用汉字显示
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口") //告诉创建的文档，这个接口是“课程查询接口”
    @PostMapping("/course/list") //RequestMapping支持post、get、put......
    //这里就定义了一个接口
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        return courseBasePageResult;
    }

    @ApiOperation("新增课程")
    @PostMapping("/course")
    /* @Validated注解声明此时需要对进入的参数进行校验，增加括号内容可以再挂载更多的约束条件 */
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDto addCourseDto) {

        //获取用户所属机构ID
        Long companyID = 1232141425L;

        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyID, addCourseDto);
        return courseBase;
    }

   /* @ApiOperation("查询课程") //废弃
    @GetMapping("/course/{id}")
    public CourseBaseInfoDto getCourseBase(@PathVariable @Validated(ValidationGroups.Select.class) String id){
        return null;
    }*/


    /**
     * @param courseId
     * @return 课程详细信息
     * @description 根据id查询接口
     */
    @ApiOperation("根据课程id查询接口")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId) {
        return courseBaseInfoService.getCourseBaseInfo(courseId);

    }

    /**
     * @param editCourseDto 新定义的一个课程编辑传输层对象
     * @return
     * @description 根据课程id和？？ 更新
     */
    @ApiOperation("修改课程功能")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated(ValidationGroups.Update.class) EditCourseDto editCourseDto) {
        Long companyId = 1232141425L; //还未接入企业id接口，所以先用硬编码写入企业id
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        return courseBaseInfoDto;

    }

}
