package com.xuecheng.system;

import com.xuecheng.system.mapper.CourseCategoryMapper;
import com.xuecheng.system.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MapperTests {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;


    @Test
    void testCourseCategoryMapper() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes();
        System.out.println(courseCategoryTreeDtos);
    }

}