package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "editCourse",description = "编辑课程信息")
public class EditCourseDto extends AddCourseDto{

    //因为大体上和新增课程相似，所以直接继承，新定义一个属性courseId
    @ApiModelProperty(value = "课程id",required = true)
    private Long id;

}
