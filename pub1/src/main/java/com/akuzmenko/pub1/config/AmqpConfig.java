package com.akuzmenko.pub1.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class AmqpConfig {

    private static final String MESSAGE_TTL = "x-message-ttl";
    private static final String DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        var factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("pub.direct");
    }

    @Bean
    public Queue queue() {
        var argumentsMap = getArguments();
        return QueueBuilder.nonDurable("pub.queue")
                .autoDelete()
                .withArguments(argumentsMap)
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue())
                .to(exchange())
                .with("pub.direct");
    }

    @Bean
    public FanoutExchange fExchange() {
        return new FanoutExchange("pub.fanout");
    }

    @Bean
    public Queue fQueue() {
        var argumentsMap = getArguments();
        return QueueBuilder.nonDurable("pub1.queue")
                .autoDelete()
                .withArguments(argumentsMap)
                .build();
    }

    @Bean
    public Binding fBinding() {
        return BindingBuilder.bind(fQueue())
                .to(fExchange());
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("pub.topic");
    }

    @Bean
    public Queue topicQueue() {
        var argumentsMap = getArguments();
        return QueueBuilder.nonDurable("pub1.topic.queue")
                .autoDelete()
                .withArguments(argumentsMap)
                .build();
    }

    @Bean
    public Binding topicBinding() {
        return BindingBuilder.bind(topicQueue())
                .to(topicExchange())
                .with(applicationName);
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.nonDurable("pub.dlq")
                .autoDelete()
                .withArgument(MESSAGE_TTL, 1000)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("pub.direct-dead-letter-exchange");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlq())
                .to(deadLetterExchange())
                .with("pub.dlq");
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    private HashMap<String, Object> getArguments() {
        var argumentsMap = new HashMap<String, Object>();
        argumentsMap.put(DEAD_LETTER_EXCHANGE, "pub.direct-dead-letter-exchange");
        argumentsMap.put(DEAD_LETTER_ROUTING_KEY, "pub.dlq");
        argumentsMap.put(MESSAGE_TTL, 1000);
        return argumentsMap;
    }
}
