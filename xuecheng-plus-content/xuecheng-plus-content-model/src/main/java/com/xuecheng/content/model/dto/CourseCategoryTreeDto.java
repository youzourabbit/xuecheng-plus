package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    // 下级节点，List中的数组就是本身。Serializable：将来需要序列化时才来实现
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
