package com.casinoroyale.transfer.player;

import java.util.UUID;

import com.casinoroyale.player.player.dto.CreatePlayerNoticeDto;
import com.casinoroyale.transfer.player.domain.PlayerFacade;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class PlayerListener {
    
    private final PlayerFacade playerFacade;

    PlayerListener(final PlayerFacade playerFacade) {
        this.playerFacade = playerFacade;
    }

    @KafkaListener(topics = "PlayerCreated")
    public void listenCreated(ConsumerRecord<String, CreatePlayerNoticeDto> kafkaMessage) {
        final CreatePlayerNoticeDto createPlayerNoticeDto = kafkaMessage.value();
        playerFacade.createPlayer(createPlayerNoticeDto);
    }

    @KafkaListener(topics = "PlayerDeleted")
    public void listenDeleted(ConsumerRecord<String, UUID> kafkaMessage) {
        final UUID playerId = kafkaMessage.value();
        playerFacade.deletePlayer(playerId);
    }

}
