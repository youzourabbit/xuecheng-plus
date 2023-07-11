package com.xuecheng.media.service.jobHandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SampleXxlJob {

    @XxlJob("shardingJobHandler")
    public void shardingJobHandler() throws Exception {

        //分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器总数

        System.out.println("shardIndex:" + shardIndex + "shardTotal:" + shardTotal);

    }

    public static Logger logger = LoggerFactory.getLogger(SampleXxlJob.class);

    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception{
        log.info("开始执行（1）");
    }

    @XxlJob("demoJobHandler2")
    public void demoJobHandler2() throws Exception{
        log.info("开始执行（2）");
    }
}
