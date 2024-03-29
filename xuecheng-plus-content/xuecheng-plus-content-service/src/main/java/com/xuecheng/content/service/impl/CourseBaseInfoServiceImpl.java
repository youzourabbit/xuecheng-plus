package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程信息管理业务接口实现类
 * @date 2022/9/6 21:45
 */
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {


    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;//课程分类

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //构建查询条件，根据课程名称查询
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        //构建查询条件，根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
//构建查询条件，根据课程发布状态查询
//todo:根据课程发布状态查询

        //分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<CourseBase> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());

        // todo:课程分类名称设置到courseBasePageResult对象中
        return courseBasePageResult;


    }

    @Transactional //模仿添加了这个注解，用法待定
    @Override
   // @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {

        // 参数合法性校验
        /*if (StringUtils.isBlank(addCourseDto.getName())) {
            throw new XueChengPlusException("课程名称为空");
        }*/
//        if (StringUtils.isBlank(addCourseDto.getMt())) {
//            throw new XueChengPlusException("课程分类为空");
//        }
//        if (StringUtils.isBlank(addCourseDto.getSt())) {
//            throw new XueChengPlusException("课程分类为空");
//        }
//        if (StringUtils.isBlank(addCourseDto.getGrade())) {
//            throw new XueChengPlusException("课程等级为空");
//        }
//        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
//            throw new XueChengPlusException("教育模式为空");
//        }
//        if (StringUtils.isBlank(addCourseDto.getUsers())) {
//            throw new XueChengPlusException("适应人群为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getCharge())) {
//            throw new XueChengPlusException("收费规则为空");
//        }
//        String charge = addCourseDto.getCharge();

        //1、向课程基本信息表course_base
        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象(使用BeanUtils代替大量的get set方法)
        BeanUtils.copyProperties(addCourseDto, courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");//创建新课程的一些默认值...
        //设置发布状态
        courseBaseNew.setStatus("203001");//创建新课程的一些默认值...
        //机构id
        courseBaseNew.setCompanyId(companyId);//机构id不能由一般页面进行访问修改，等同于只读
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());//创建新课程的一些默认值...
        //插入课程基本信息表
        //CourseMarket courseMarketNew = new CourseMarket();
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0) {
            throw new XueChengPlusException("新增课程基本信息失败");
        }
//todo:向课程营销表保存课程营销信息
//todo:查询课程基本信息及营销信息并返回


        //2、向课程营销表course_market
        CourseMarket courseMarketNew = new CourseMarket();
        //  将页面输入的数据拷贝到courseMarketNew
        BeanUtils.copyProperties(addCourseDto, courseMarketNew);
        // 课程id
        Long courseId = courseBaseNew.getId();
        courseMarketNew.setId(courseId);
        // 保存营销信息
        savaCourseMarket(courseMarketNew);
        // 从数据库查询课程的详细信息，包括两部分
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
/*
        if (charge.equals("201001")){
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue()<=0){
                throw new XueChengPlusException("课程的价格不能为空并必须大于0");
            }
        }*/
        return courseBaseInfo;
    }

    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {

        // 查询课程功能实现
        /*CourseBase courseBase = courseBaseMapper.selectById(courseId);

        return null;*/

        //查询课程信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        //查询营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //要返回的对象
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;

    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {

        //1、数据合法性校验————根据具体业务逻辑去校验
        // 本机构只能修改本机构的课程————companyId
        Long courseId = editCourseDto.getId();//先获取课程id核验，再进行核验
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseId == null){
            XueChengPlusException.cast("课程不存在");//返回错误信息，如果课程信息不存在就直接停止

        }
        if (!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        // 2、封装数据
        BeanUtils.copyProperties(editCourseDto,courseBase);
        // （注意按数据库内部字段逻辑分析需要额外修改的字段）
        // 修改时间
        courseBase.setChangeDate(LocalDateTime.now());// （没看懂）

        // 3、更新数据库
        int i = courseBaseMapper.updateById(courseBase);
        if (i<=0){
            XueChengPlusException.cast("更新课程失败");
        }
        // 查询课程信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);

        return courseBaseInfo;
    }

    // 查询课程信息
    private CourseBaseInfoDto getCourseBaseInfo(long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase==null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        //组装
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);//(原始对象，目标对象)
        if (courseMarket!=null){

            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        // 返回一个数据传输流对象
        return courseBaseInfoDto;
    }

    //单独写一个方法，保存course_market营销信息
    private int savaCourseMarket(CourseMarket courseMarketNew) {
        String charge = courseMarketNew.getCharge();
        // 参数的合法性校验
        if (charge.isEmpty()) {
            throw new XueChengPlusException("收费规则为空");
        }
        if (charge.equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice() <= 0) {
                throw new XueChengPlusException("课程价格不能为空并且不能等于0");
            }
        }

        // 从数据库查询营销信息，存在则更新，不存在则添加
        Long id = courseMarketNew.getId();
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        if(courseMarket==null){
            // 插入数据库
            int insert = courseMarketMapper.insert(courseMarketNew);
            return insert;
        }else{
            // 将courseMarketNew拷贝courseMarket
            BeanUtils.copyProperties(courseMarketNew,courseMarket);
            courseMarket.setId(courseMarketNew.getId());
            // 更新
            int update = courseMarketMapper.updateById(courseMarket);
            return update;
        }
    }

}
