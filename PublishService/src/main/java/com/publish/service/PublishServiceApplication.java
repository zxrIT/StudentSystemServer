package com.publish.service;

import com.common.rabbitmq.Configuration.MessageConverterConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@MapperScan("com.publish.service.repository")
@Import({MessageConverterConfiguration.class})
public class PublishServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PublishServiceApplication.class, args);
    }
}
