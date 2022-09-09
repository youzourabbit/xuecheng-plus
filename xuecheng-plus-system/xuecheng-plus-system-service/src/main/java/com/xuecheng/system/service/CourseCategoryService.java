package com.xuecheng.system.service;

import com.xuecheng.system.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * <p>
 * 课程分类 服务类
 * </p>
 *
 * @author itcast
 */
public interface CourseCategoryService  {

    /**
     * 课程分类树形结构查询
     * @return
     */
    public List<CourseCategoryTreeDto> queryTreeNodes();


}
