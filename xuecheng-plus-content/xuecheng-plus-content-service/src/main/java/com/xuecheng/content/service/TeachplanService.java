package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @author = Ming
 * @date = 2023/5/10
 * @time  = 0:16
 */

public interface TeachplanService {
    /**
     * 根据课程id查询课程计划
     * @param courseId 课程id
     * @return 课程计划
     */
    List<TeachplanDto> findTeachplanTree(Long courseId);

    void saveTeachplan(SaveTeachplanDto teachplanDto);

    void deleteTeachplan(Long courseId);

    void moveTeachplan(Long courseId,String moveValue);
}
