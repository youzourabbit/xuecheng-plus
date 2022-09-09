package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description 课程基本信息dto
 * @author Mr.M
 * @date 2022/9/7 17:44
 * @version 1.0
 */
@Data
public class CourseBaseInfoDto extends CourseBase {


 /**
  * 收费规则，对应数据字典
  */
 private String charge;

 /**
  * 价格
  */
 private Float price;


}
