package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("course_base")
public class CourseBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机构ID
     */
//    @TableField("company_id")
    private Long companyId;

    /**
     * 机构名称
     */
//    @TableField("company_name")
    private String companyName;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 适用人群
     */
    private String users;

    /**
     * 课程标签
     */
    private String tags;

    /**
     * 大分类
     */
    private String mt;

    /**
     * 课程大类名称
     */
//    @TableField("mt_name")
    private String mtName;

    /**
     * 小分类
     */
    private String st;

    /**
     * 课程小类名称
     */
//    @TableField("st_name")
    private String stName;

    /**
     * 课程等级
     */
    private String grade;

    /**
     * 教育模式(common普通，record 录播，live直播等）
     */
    private String teachmode;

    /**
     * 课程介绍
     */
    private String description;

    /**
     * 课程图片
     */
    private String pic;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime changeDate;

    /**
     * 创建人
     */
//    @TableField("create_people")
    private String createPeople;

    /**
     * 更新人
     */
//    @TableField("change_people")
    private String changePeople;

    /**
     * 审核状态
     */
//    @TableField("audit_status")
    private String auditStatus;



    /**
     * 未发布，已发布 下线
     */
    private String status;

    /**
     * 课程发布标识
     */
//    @TableField("course_pub_id")
    private Long coursePubId;

    /**
     * 课程发布时间
     */
//    @TableField("course_pub_date")
    private LocalDateTime coursePubDate;


}
