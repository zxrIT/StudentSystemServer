package com.curriculum.management.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.curriculum.management.service.repository")
public class CurriculumManagementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CurriculumManagementServiceApplication.class, args);
    }
}
