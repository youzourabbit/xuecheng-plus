package com.xuecheng.content.service;

import org.springframework.stereotype.Service;

@Service
public interface Money {

    void transfer(String in,String out,int money);
}
