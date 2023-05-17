package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
@RestController
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

    // 查询课程计划
    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程Id", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    private List<TeachplanDto> getTeachNodes(@PathVariable Long courseId) {

        //此处留有bug返回的数据将会遗漏一些没有子节点的数据
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        return teachplanTree;
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    private void saveTeachplan(@RequestBody SaveTeachplanDto teachplanDto) {
// 添加章还是添加小节，由请求体中的json文件决定
        teachplanService.saveTeachplan(teachplanDto);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("/teachplan/{courseId}")
    private void deleteTeachplan(@PathVariable Long courseId) {
        teachplanService.deleteTeachplan(courseId);
    }

    @ApiOperation("课程计划排序")
    @PostMapping("/teachplan/{moveValue}/{courseId}")
    private void moveTeachplan(@PathVariable Long courseId, @PathVariable String moveValue){
        teachplanService.moveTeachplan(courseId,moveValue);
    }

}
