package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : Ming
 * @Date: 2023/7/11 8:59
 * @ProjectName: xuecheng-plus
 * @PackageName: com.xuecheng.content.model.dto
 * @ClassName: BindTeachplanMediaDto
 * @Description: TODO
 * @Version: 1.0
 */

@Data
public class BindTeachPlanMediaDto {

    @ApiModelProperty(value = "媒资文件id",required = true)
    String mediaId;

    @ApiModelProperty(value = "媒资文件名称",required = true)
    String fileName;

    @ApiModelProperty(value = "课程计划标识",required = true)
    Long teachplanId;
}
