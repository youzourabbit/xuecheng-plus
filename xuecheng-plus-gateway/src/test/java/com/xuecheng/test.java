package com.xuecheng;

import com.alibaba.nacos.common.JustForTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class test {

    @JustForTest
    public static void main(String[] args) throws IOException {

        ProcessBuilder builder = new ProcessBuilder();
        builder.command("C:\\Program Files (x86)\\Sublime Text 3.3126x86\\sublime_text.exe");
        builder.redirectErrorStream(true);
        builder.start();
    }
}
