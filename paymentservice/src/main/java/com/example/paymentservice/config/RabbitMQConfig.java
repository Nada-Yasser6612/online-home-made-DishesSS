package com.example.paymentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queue names
    public static final String ORDER_VALIDATION_REQUEST_QUEUE = "order.validation.request.queue";
    public static final String ORDER_VALIDATION_RESPONSE_QUEUE = "order.validation.response.queue";

    // Exchange name
    public static final String ORDER_DIRECT_EXCHANGE = "order.direct.exchange";

    // Routing keys
    public static final String ORDER_VALIDATION_REQUEST_ROUTING_KEY = "order.validation.request";

    // 1. Declare Queues
    @Bean
    public Queue requestQueue() {
        return new Queue(ORDER_VALIDATION_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(ORDER_VALIDATION_RESPONSE_QUEUE, true);
    }

    // 2. Declare Exchange
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(ORDER_DIRECT_EXCHANGE);
    }

    // 3. Bind requestQueue to the exchange using routing key
    @Bean
    public Binding bindingRequestQueue(Queue requestQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(requestQueue).to(directExchange).with(ORDER_VALIDATION_REQUEST_ROUTING_KEY);
    }

    // 4. Set up JSON converter
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());

        // Optional: remove __TypeId__ to avoid type mismatch
        template.setBeforePublishPostProcessors(message -> {
            message.getMessageProperties().getHeaders().remove("__TypeId__");
            return message;
        });

        return template;
    }
    @Bean
    public DirectExchange paymentsExchange() {
        return new DirectExchange("payments.exchange");
    }



}
