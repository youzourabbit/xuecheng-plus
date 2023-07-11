package com.xuecheng.content.handler;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: Ming
 * @Date: 2023/7/11 14:35
 * @ProjectName: xuecheng-plus
 * @PackageName: com.xuecheng.content.handler
 * @ClassName: xxlJobContent
 * @Description: TODO
 * @Version: 1.0
 */

@Component
@Slf4j
public class xxlJobContent {

    @XxlJob("CoursePublishJobHandler")
    public void sample(){
        log.debug(">>>>>>>>>>>>>>>>>>模拟截获课程发布任务（功能未实现）");
    }
}
