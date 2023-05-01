package com.xuecheng.content;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 内容管理服务的启动类
 */
@SpringBootApplication //启动类，创建的目录决定它将会扫描所有的content包
@EnableSwagger2Doc //添加注解使本模块支持Swagger
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
