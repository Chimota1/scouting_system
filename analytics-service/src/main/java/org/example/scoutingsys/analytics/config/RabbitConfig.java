package org.example.scoutingsys.analytics.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
    public static final String EXCHANGE = "scouting.events";
    public static final String QUEUE = "analytics.club-events";

    @Bean
    public TopicExchange scoutingEventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue clubEventsQueue() {
        return new Queue(QUEUE, true, false, false);
    }

    @Bean
    public Binding clubEventsBinding(Queue clubEventsQueue, TopicExchange scoutingEventsExchange) {
        return BindingBuilder.bind(clubEventsQueue).to(scoutingEventsExchange).with("club.#");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
