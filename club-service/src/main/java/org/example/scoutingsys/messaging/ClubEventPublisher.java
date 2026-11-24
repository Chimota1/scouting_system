package org.example.scoutingsys.messaging;

import org.example.scoutingsys.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Component
public class ClubEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ClubEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String eventType, Long clubId, String clubName, Long leagueId) {
        ClubChangedEvent event = new ClubChangedEvent(
                UUID.randomUUID().toString(), eventType, clubId, clubName, leagueId, java.time.Instant.now());

        Runnable publish = () -> rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE, "club." + eventType.toLowerCase(), event);

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish.run();
                }
            });
        } else {
            publish.run();
        }
    }
}
