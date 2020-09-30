package com.casinoroyale.transfer.team;

import java.math.BigDecimal;
import java.util.UUID;

import com.casinoroyale.team.team.dto.CreateTeamNoticeDto;
import com.casinoroyale.transfer.team.domain.TeamFacade;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class TeamListener {
    
    private final TeamFacade teamFacade;

    @Autowired
    TeamListener(final TeamFacade teamFacade) {
        this.teamFacade = teamFacade;
    }

    @KafkaListener(topics = "TeamCreated")
    public void listenCreated(ConsumerRecord<String, CreateTeamNoticeDto> kafkaMessage) {
        final CreateTeamNoticeDto createTeamNoticeDto = kafkaMessage.value();
        teamFacade.createTeam(createTeamNoticeDto);
    }
    
    @KafkaListener(topics = "TeamUpdated")
    public void listenUpdated(ConsumerRecord<UUID, BigDecimal> kafkaMessage) {
        final UUID teamId = kafkaMessage.key();
        final BigDecimal newCommissionRate = kafkaMessage.value();
        
        teamFacade.updateTeam(teamId, newCommissionRate);
    }    
    
    @KafkaListener(topics = "TeamDeleted")
    public void listenDeleted(ConsumerRecord<String, UUID> kafkaMessage) {
        final UUID teamId = kafkaMessage.value();
        
        teamFacade.deleteTeam(teamId);
    }

}
