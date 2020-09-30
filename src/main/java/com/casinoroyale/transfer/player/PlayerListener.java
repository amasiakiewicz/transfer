package com.casinoroyale.transfer.player;

import java.util.UUID;

import com.casinoroyale.transfer.player.domain.PlayerFacade;
import com.casinoroyale.transfer.player.dto.CreatePlayerDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class PlayerListener {
    
    private final PlayerFacade playerFacade;

    @Autowired
    PlayerListener(final PlayerFacade playerFacade) {
        this.playerFacade = playerFacade;
    }

    @KafkaListener(topics = "PlayerCreated")
    public void listenCreated(ConsumerRecord<String, CreatePlayerDto> kafkaMessage) {
        final CreatePlayerDto createPlayerDto = kafkaMessage.value();
        playerFacade.createPlayer(createPlayerDto);
    }        
    
    @KafkaListener(topics = "PlayerUpdated")
    public void listenUpdated(ConsumerRecord<UUID, UUID> kafkaMessage) {
        final UUID playerId = kafkaMessage.key();
        final UUID newTeamId = kafkaMessage.value();
        
        playerFacade.updatePlayer(playerId, newTeamId);
    }    
    
    @KafkaListener(topics = "PlayerDeleted")
    public void listenDeleted(ConsumerRecord<String, UUID> kafkaMessage) {
        final UUID playerId = kafkaMessage.value();
        playerFacade.deletePlayer(playerId);
    }

}
