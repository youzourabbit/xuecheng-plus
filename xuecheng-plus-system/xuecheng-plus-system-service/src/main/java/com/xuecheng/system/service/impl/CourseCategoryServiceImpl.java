package com.xuecheng.system.service.impl;

import com.xuecheng.system.mapper.CourseCategoryMapper;
import com.xuecheng.system.model.dto.CourseCategoryTreeDto;
import com.xuecheng.system.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {


    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    public List<CourseCategoryTreeDto> queryTreeNodes() {

        return courseCategoryMapper.selectTreeNodes();
    }

}
