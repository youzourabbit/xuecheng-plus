package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.TbMoneyMapper;
import com.xuecheng.content.service.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MoneyImpl implements Money {

    @Autowired
    TbMoneyMapper moneyDao;

    @Override
    public void transfer(String in, String out, int money) {
        moneyDao.inMoney(in, money);
        moneyDao.outMoney(out, money);
    }
}
