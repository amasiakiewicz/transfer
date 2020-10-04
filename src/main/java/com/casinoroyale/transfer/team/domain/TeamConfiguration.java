package com.casinoroyale.transfer.team.domain;

import com.casinoroyale.transfer.exchangerate.domain.ExchangeRateFacade;
import com.casinoroyale.transfer.player.domain.PlayerFacade;
import com.casinoroyale.transfer.team.dto.FeePlayerTransferredNoticeDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
class TeamConfiguration {

    @Bean
    TeamFacade teamFacade(
            final ExchangeRateFacade exchangeRateFacade, final PlayerFacade playerFacade,
            final KafkaTemplate<String, FeePlayerTransferredNoticeDto> kafkaTemplate, final TeamRepository teamRepository
    ) {
        return new TeamFacade(
                exchangeRateFacade, playerFacade, kafkaTemplate, teamRepository
        );
    }

}
