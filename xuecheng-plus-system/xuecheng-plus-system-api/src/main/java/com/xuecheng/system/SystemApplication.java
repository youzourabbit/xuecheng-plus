package com.xuecheng.system;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 *     系统管理启动类
 * </p>
 *
 * @Description:
 */
@EnableScheduling
@EnableSwagger2Doc
@SpringBootApplication
public class SystemApplication {

    @Value("${testconfig.a}")
    String a;
    @Value("${testconfig.b}")
    String b;
    @Value("${testconfig.c}")
    String c;
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class,args);
    }

    @Bean
    public Integer getA(){
        System.out.println("a="+a);
        System.out.println("b="+b);
        System.out.println("c="+c);
        return new Integer(1);
    }
}