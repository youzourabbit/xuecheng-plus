package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.*;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @description 课程基本信息管理业务接口
 * @author Mr.M
 * @date 2022/9/6 21:42
 * @version 1.0
 */
public interface TeachplanService {

/**
 * @description 查询课程计划树型结构
 * @param courseId 课程id
 * @return java.util.List<com.xuecheng.content.model.dto.TeachplanDto>
 * @author Mr.M
 * @date 2022/9/9 12:52
*/
 public List<TeachplanTreeDto> findTeachplayTree(long courseId);

 /**
  * @description 只在课程计划
  * @param teachplanDto  课程计划信息
  * @return void
  * @author Mr.M
  * @date 2022/9/9 13:39
 */
 public void saveTeachplan(SaveTeachplanDto teachplanDto);

 /**
  * @description 删除课程计划
  * @param teachPlanId 课程计划id
  * @return void
  * @author Mr.M
  * @date 2022/9/9 18:57
 */
 public void removeTeachPlan(Long teachPlanId);

 /**
  * @description 移动课程计划
  * @param teachPlanId 课程计划id
  * @return void
  * @author Mr.M
  * @date 2022/9/9 20:35
 */
 public void moveTeachPlan(Long teachPlanId,String moveType);

 public TeachplanMedia associationMedia(BindTeachPlanMediaDto bindTeachplanMediaDto);

 }
