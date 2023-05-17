package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.execption.XueChengPlusException;
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

            // 定义一个查询用的容器，限定范围指定在courseId，parentId
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

    private Teachplan getTeachplan(Long courseId) {
        return teachplanMapper.selectById(courseId);
    }

    @Override
    public void deleteTeachplan(Long courseId) {

        Teachplan teachplan1 = getTeachplan(courseId);
        TeachplanDto teachplanDto = new TeachplanDto();
        BeanUtils.copyProperties(teachplan1, teachplanDto);
        Long parentid1 = teachplan1.getParentid();
        if (parentid1 == 0L) {
            List<Teachplan> teachplans = teachplanMapper.selectByParentId(teachplanDto.getId());
            if (!teachplans.isEmpty()) {
                XueChengPlusException.cast(120409, "本章节还有课程存在，无法删除");
            }
        }
        teachplanMapper.deleteById(teachplanDto);
        //TeachplanDto teachplanDto = teachplanTree.get(0);
        // 判断父节点id是否为0
        //Long parentid = teachplanDto.getParentid();
        /*if (parentid == 0) {
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, teachplan);
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper = queryWrapper.eq(Teachplan::getParentid, courseId);//尝试：由于判定了本节点是父节点，所以以本节点的courseId当做查询条件的parentId
            int count = teachplanMapper.selectCount(queryWrapper);
            if (count > 0) {
                XueChengPlusException.cast(120409, "本章节还有课程存在，无法删除");
            }else {
                teachplanMapper.deleteById(teachplanDto);
            }
        }else {
            teachplanMapper.deleteById(teachplanDto);
        }*/
    }

    @Override
    public void moveTeachplan(Long courseId, String moveValue) {
        Teachplan teachplan = teachplanMapper.selectById(courseId);
        Integer orderby = teachplan.getOrderby();
        Long parentid = teachplan.getParentid();
        if (moveValue.equals("moveup")) {
            // 需要获取orderby数据
            if (orderby > 1) {
                Teachplan plan_top = new Teachplan();
                plan_top.setOrderby(orderby - 1);
                plan_top.setParentid(parentid);
                plan_top = teachplanMapper.selectById(plan_top);
                plan_top.setOrderby(orderby);
                teachplan.setOrderby(orderby - 1);
                teachplanMapper.updateById(plan_top);
                teachplanMapper.updateById(teachplan);
            } else {
                XueChengPlusException.cast("已经处于本章最顶部");
            }
        } else if (moveValue.equals("movedown")) {
            Teachplan plan_bottom = new Teachplan();
            plan_bottom.setParentid(parentid);
            plan_bottom.setOrderby(orderby+1);
            plan_bottom = teachplanMapper.selectById(plan_bottom);
            if (plan_bottom != null) {
                plan_bottom.setOrderby(orderby);
                teachplan.setOrderby(orderby + 1);
                teachplanMapper.updateById(plan_bottom);
                teachplanMapper.updateById(teachplan);
            } else {
                XueChengPlusException.cast("已经处于本章最底部");
            }
        } else {
            XueChengPlusException.cast("未知错误");
        }
    }

}
