package com.xuecheng.system.controller;

import com.xuecheng.system.model.po.Dictionary;
import com.xuecheng.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author itcast
 */
@Slf4j
@RefreshScope
@RestController
public class DictionaryController  {

    @Value("${testconfig.a}")
    String a;
    @Value("${testconfig.b}")
    String b;
    @Value("${testconfig.c}")
    String c;

    @Autowired
    private DictionaryService  dictionaryService;

    @GetMapping("/dictionary/all")
    public List<Dictionary> queryAll() {
        System.out.println("a="+a);
        System.out.println("b="+b);
        System.out.println("c="+c);
        return dictionaryService.queryAll();
    }

    @GetMapping("/dictionary/code/{code}")
    public Dictionary getByCode(@PathVariable String code) {
        return dictionaryService.getByCode(code);
    }
}
