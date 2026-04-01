package com.mallchat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mallchat.mapper")
public class MallChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallChatApplication.class, args);
    }
}
