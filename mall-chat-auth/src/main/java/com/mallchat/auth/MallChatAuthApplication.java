package com.mallchat.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** TODO: update docs. */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.mallchat.auth.client")
@MapperScan({"com.mallchat.auth.mapper", "com.mallchat.auth.oauth.mapper"})
public class MallChatAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallChatAuthApplication.class, args);
    }
}
