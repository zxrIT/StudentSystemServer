package com.course.selection.service.component;

import com.common.rabbitmq.rabbitMQContent.SelectionCourseMQContent;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@SuppressWarnings("all")
public class RabbitMQSelection {
    @Bean
    @DependsOn("selectionCourseQueue")
    public DirectExchange selectionCourseDelayExchange() {
        return ExchangeBuilder.directExchange(SelectionCourseMQContent.EXCHANGE_NAME)
                .delayed().durable(true).build();
    }

    @Bean
    public Queue selectionCourseQueue() {
        return new Queue(SelectionCourseMQContent.QUEUE_NAME);
    }

    @Bean
    @DependsOn({"selectionCourseQueue", "selectionCourseDelayExchange"})
    public Binding selectionCourseBinding() {
        return BindingBuilder.bind(selectionCourseQueue()).to(selectionCourseDelayExchange())
                .withQueueName();
    }
}
