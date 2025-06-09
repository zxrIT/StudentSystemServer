package com.course.selection.service;

import com.common.rabbitmq.Configuration.MessageConverterConfiguration;
import com.common.userInfo.Configuration.MvcConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.course.selection.service.repository")
@Import({MessageConverterConfiguration.class, MvcConfig.class})
public class CourseSelectionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseSelectionServiceApplication.class, args);
    }
}
