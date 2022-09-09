package com.xuecheng.content;

import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.TeachplanTreeDto;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CourseBaseMapperTests {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    TeachplanMapper teachplanMapper;


    @Test
    void testCourseBaseMapper() {
        CourseBase courseBase = courseBaseMapper.selectById(74L);
        Assertions.assertNotNull(courseBase);
    }
    @Test
    void testTeachplanMapper() {
        List<TeachplanTreeDto> teachplanDtos = teachplanMapper.selectTreeNodes(74);
        Assertions.assertNotNull(teachplanDtos);
    }

}