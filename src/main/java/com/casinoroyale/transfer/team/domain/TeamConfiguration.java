package com.casinoroyale.transfer.team.domain;

import java.util.UUID;

import com.casinoroyale.transfer.exchangerate.domain.ExchangeRateFacade;
import com.casinoroyale.transfer.player.domain.PlayerFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
class TeamConfiguration {

    @Bean
    TeamFacade teamFacade(
            final ExchangeRateFacade exchangeRateFacade, final PlayerFacade playerFacade, 
            final KafkaTemplate<UUID, UUID> kafkaTemplate, final TeamRepository teamRepository
    ) {
        return new TeamFacade(
                exchangeRateFacade, playerFacade, kafkaTemplate, teamRepository
        );
    }

}
