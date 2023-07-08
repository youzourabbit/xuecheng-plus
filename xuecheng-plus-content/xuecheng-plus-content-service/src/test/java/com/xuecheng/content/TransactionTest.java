package com.xuecheng.content;

import com.xuecheng.content.service.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionTest {

    @Autowired
    private Money money;

    @Test
    public void transferTest() {
        System.out.println("Hello World!");
        money.transfer("Tom", "Jerry", 100);
    }
}
