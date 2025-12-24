package com.health.agent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.health.agent.module.*.mapper")
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
        System.out.println("===========================================");
        System.out.println("饮食健康管理系统后端服务启动成功！");
        System.out.println("接口文档地址: http://localhost:8080/doc.html");
        System.out.println("===========================================");
    }
}

