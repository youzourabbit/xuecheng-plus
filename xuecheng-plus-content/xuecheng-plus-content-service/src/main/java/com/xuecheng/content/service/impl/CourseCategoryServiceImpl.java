package com.xuecheng.content.service.impl;


import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) { //1、调用本方法，传进一个根节点id，返回一个CourseCategoryTreeDto对象列表

        //List<CourseCategoryTreeDto>中的CourseCategoryTreeDto对象只保存一个对象，就是List<CourseCategoryTreeDto>对象

        //调用mapper，递归查询出分类信息
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNode(id);// 2、从服务模块获取到id节点的所有子级以下节点
//    封装成List<CourseCategoryTreeDto>

//        先将List转成Map，key就是节点的id，value就是CourseCategoryTreeDto对象，目的就是为了方便从map获取节点
        Map<String,CourseCategoryTreeDto> mapTmp = courseCategoryTreeDtos.stream().
                filter(/*进行一次过滤，排除所有目前map中id与根节点id相同的部分，就可以获取到纯子节点map*/item->!id.equals(item.getId())).
                collect(Collectors.toMap(/*map中有key与value*/key -> key.getId(), value -> value,
                /*如若key重复，对key进行选择*/(key1, key2) -> key1));// 3、对所有子节点进行转换、过滤
        //定义一个List作为最终返回的List
        List<CourseCategoryTreeDto> courseCategoryList =new ArrayList<>();
//        从头遍历List<CourseCategoryTreeDto>，一边遍历一边找子节点，放在父节点的childrenNodes上
        //courseCategoryTreeDtos.stream().collect(Collectors.toMap(/*map中有key与value*/key -> key.getId(), value -> value,
                /*如若key重复，对key进行选择*/ /*(key1, key2) -> key1));*/

        courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId())).forEach(item -> {
            // 开始处理
            if (item.getParentid().equals(id)) {//如果父节点与传进的id相同，将遍历到的数据收纳为子节点
                courseCategoryList.add(item);
            }

            // 找到节点的父节点
            CourseCategoryTreeDto courseCategoryParent = mapTmp.get(item.getParentid());

            //CourseCategoryTreeDto courseCategoryTreeDto = mapTmp.get(item.getParentid());//数据优化--完善内一个节点的子节点创建
            if (courseCategoryParent != null) {
                if (courseCategoryParent.getChildrenTreeNodes() == null) {//如果本节点没有子节点列表，则创建新的列表，不论这个节点实际上有或没有子节点
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                //到每个节点的子节点，放在父节点的childrenTreeNodes中
                courseCategoryParent.getChildrenTreeNodes().add(item);
            }
        });
        return courseCategoryList;
    }
}
