package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "课程分类接口",tags = "课程分类接口")
@RestController
public class CourseCategoryController {// ?估计是创建了一个模型类 ==>controller修改，因为已经实现了分类查询，对返回值进行实现
    //定义接口:进行接口需求分析--搞定数据模型--定义模型类--定义接口

    @Autowired
    CourseCategoryService courseCategoryService;

    @GetMapping("course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        // 此时需要对子节点进行处理
        return courseCategoryService.queryTreeNodes("1");//本方法是查询所有，直接把根节点放进即可。返回的是一个根节点带着若干子节点，不过
    }
}
