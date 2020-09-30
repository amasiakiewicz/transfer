package com.casinoroyale.transfer.player.domain;

import static com.casinoroyale.transfer.TransferApplication.DEFAULT_ZONE_OFFSET;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.casinoroyale.transfer.player.dto.CreatePlayerNoticeDto;
import org.junit.jupiter.api.Test;

class PlayerTest {
    
    @Test
    void shouldCalculateTransferFee() {
        //given
        long monthsOfExperience = 5;
        long age = 32;
        final Player player = givenPlayer(monthsOfExperience, age);
        
        //when
        final BigDecimal transferFee = player.calculateTransferFee();

        //then
        assertThat(transferFee).isEqualByComparingTo("15625");
    }

    private Player givenPlayer(final long monthsOfExperience, final long age) {
        final LocalDate now = LocalDate.now(DEFAULT_ZONE_OFFSET);
        final LocalDate dateOfBirth = now.minusYears(age);
        final LocalDate playStart = now.minusMonths(monthsOfExperience);

        final CreatePlayerNoticeDto createPlayerNoticeDto = new CreatePlayerNoticeDto(randomUUID(), randomUUID(), dateOfBirth, playStart);
        return Player.create(createPlayerNoticeDto);
    }
}
