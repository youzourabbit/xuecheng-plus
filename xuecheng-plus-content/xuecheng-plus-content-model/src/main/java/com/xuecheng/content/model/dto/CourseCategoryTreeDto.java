package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description 课程分类树型结点dto
 * @author Mr.M
 * @date 2022/9/7 15:16
 * @version 1.0
 */
@Data
@ApiModel(value = "CourseCategoryTreeDto",description = "查询所有教学计划树形结构数据")
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    List<CourseCategoryTreeDto> childrenTreeNodes;
}
