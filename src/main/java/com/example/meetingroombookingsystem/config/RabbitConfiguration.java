package com.example.meetingroombookingsystem.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息队列配置，包括订单延迟取消和邮件队列
 */
@Configuration
public class RabbitConfiguration {

    // 订单超时取消相关常量
    public static final String ORDER_EXCHANGE = "order.event.exchange";
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String DEAD_LETTER_QUEUE = "order.release.queue";
    public static final String ORDER_ROUTING_KEY = "order.delay.release";
    public static final String DEAD_ROUTING_KEY = "order.release";

    /**
     * 保留原有 mail 队列
     */
    @Bean("mailQueue")
    public Queue mailQueue(){
        return QueueBuilder
                .durable("mail")
                .build();
    }

    /**
     * 延迟订单队列：消息进入此队列，延迟后进入死信队列
     */
    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(ORDER_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_ROUTING_KEY)
                .build();
    }

    /**
     * 死信队列：接收超时未支付订单
     */
    @Bean
    public Queue releaseQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public DirectExchange orderEventExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder
                .bind(delayQueue())
                .to(orderEventExchange())
                .with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding releaseBinding() {
        return BindingBuilder
                .bind(releaseQueue())
                .to(orderEventExchange())
                .with(DEAD_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
