package com.publish.service.component;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@SuppressWarnings("all")
public class RabbitMQPublish {

    @Bean
    @DependsOn("publishCourseQueue")
    public DirectExchange publishCourseDelayExchange() {
        return ExchangeBuilder.directExchange("student.publish.course.delay.exchange")
                .delayed().durable(true).build();
    }

    @Bean
    public Queue publishCourseQueue() {
        return new Queue("publishCourseQueue");
    }

    @Bean
    @DependsOn({"publishCourseQueue", "publishCourseDelayExchange"})
    public Binding publishCourseBinding() {
        return BindingBuilder.bind(publishCourseQueue()).to(publishCourseDelayExchange())
                .withQueueName();
    }

    @Bean
    @DependsOn("courseSelectQueue")
    public DirectExchange courseSelectExchange() {
        return ExchangeBuilder.directExchange("student.course.select.exchange")
                .delayed().durable(true).build();
    }

    @Bean
    public Queue courseSelectQueue() {
        return new Queue("courseSelectQueue");
    }

    @Bean
    @DependsOn({"courseSelectQueue", "courseSelectExchange"})
    public Binding courseSelectBinding() {
        return BindingBuilder.bind(courseSelectQueue()).to(courseSelectExchange()).withQueueName();
    }
}
