package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author = Ming
 * @date = 2023/5/10
 * @time = 0:20
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {

        //根据传进的课程计划id是否为空来判断是修改操作还是新增操作
        Long teachplanId = teachplanDto.getId();
        if (teachplanId == null) {//使用解决bug方案2：创建新课程时同时创建一个新小节
            // 新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, teachplan);//将数据按指定格式封装后，再执行insert操作

            Long parentid = teachplanDto.getParentid();// 查询父节点id
            Long courseId = teachplanDto.getCourseId();// 查询课程id
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
            // 查询符合条件的数据字段数量(课程id，父节点id)(课程id是一整个分类，就比如课程id117)
            Integer count = teachplanMapper.selectCount(queryWrapper);
            teachplan.setOrderby(count + 1);//courseId为count+1的话，就再指定一个子节点

            teachplanMapper.insert(teachplan);//
        } else {
            // 修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            // 将参数复制到teachplan
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long courseId) {
        teachplanMapper.deleteById(courseId);
    }

}
