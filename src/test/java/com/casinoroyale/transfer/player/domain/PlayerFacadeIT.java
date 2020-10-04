package com.casinoroyale.transfer.player.domain;

import static com.casinoroyale.transfer.TransferApplication.DEFAULT_ZONE_OFFSET;
import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration.builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.casinoroyale.player.player.dto.CreatePlayerNoticeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PlayerFacadeIT {

    @Autowired //SUT
    private PlayerFacade playerFacade;
    
    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void shouldProvideTeamId() {
        //given
        final UUID expectedTeamId = randomUUID();
        final UUID playerId = givenPlayerInDb(expectedTeamId);

        //when
        final UUID teamId = playerFacade.findTeamByPlayer(playerId);

        //then
        assertThat(teamId).isEqualTo(expectedTeamId);
    }

    @Test
    void shouldCalculateTransferFee() {
        //given
        final long monthsOfExperience = 13;
        final long age = 36;
        final UUID playerId = givenPlayerInDb(monthsOfExperience, age);

        //when
        final BigDecimal transferFee = playerFacade.calculateTransferFee(playerId);

        //then
        assertThat(transferFee).isEqualByComparingTo("36111.11");
    }

    @Test
    void shouldCreatePlayer() {
        //given
        final UUID playerId = randomUUID();
        final UUID teamId = randomUUID();
        final LocalDate dateOfBirth = LocalDate.of(2003, 10, 15);
        final LocalDate playStart = LocalDate.of(2012, 3, 1);
        final CreatePlayerNoticeDto createPlayerNoticeDto = new CreatePlayerNoticeDto(playerId, teamId, dateOfBirth, playStart);

        //when
        playerFacade.createPlayer(createPlayerNoticeDto);

        //then
        assertThat(existingPlayerInDb(playerId))
                .usingRecursiveComparison(builder().withIgnoredFields("version", "createdDateTime").build())
                .isEqualTo(expectedPlayer(playerId, teamId, dateOfBirth, playStart));
    }

    @Test
    void shouldUpdatePlayerOnlyWithTeamId() {
        //given
        final UUID playerId = randomUUID();
        final UUID oldTeamId = randomUUID();
        final LocalDate dateOfBirth = LocalDate.of(2003, 10, 15);
        final LocalDate playStart = LocalDate.of(2012, 3, 1);
        givenPlayerInDb(playerId, oldTeamId, dateOfBirth, playStart);
        final UUID newTeamId = randomUUID();

        //when
        playerFacade.updatePlayer(playerId, newTeamId);

        //then
        assertThat(existingPlayerInDb(playerId))
                .usingRecursiveComparison(builder().withIgnoredFields("version", "createdDateTime").build())
                .isEqualTo(expectedPlayer(playerId, newTeamId, dateOfBirth, playStart));
    }

    @Test
    void shouldDeletePlayer() {
        //given
        final UUID playerId = givenPlayerInDb();

        //when
        playerFacade.deletePlayer(playerId);

        //then
        assertThat(playerId).satisfies(this::doesntExistInDb);
    }

    private void doesntExistInDb(final UUID playerId) {
        final boolean exists = playerRepository.existsById(playerId);
        assertThat(exists).isFalse();
    }

    private UUID givenPlayerInDb() {
        final UUID teamId = randomUUID();
        return givenPlayerInDb(teamId);
    }

    private void givenPlayerInDb(final UUID playerId, final UUID teamId, final LocalDate dateOfBirth, final LocalDate playStart) {
        final Player player = new Player(playerId, teamId, dateOfBirth, playStart);
        playerRepository.save(player);
    }

    private Player expectedPlayer(final UUID playerId, final UUID teamId, final LocalDate dateOfBirth, final LocalDate playStart) {
        return new Player(playerId, teamId, dateOfBirth, playStart);
    }

    private Player existingPlayerInDb(final UUID playerId) {
        return playerRepository
                .findById(playerId)
                .orElseThrow(IllegalStateException::new);
    }
    
    private UUID givenPlayerInDb(final UUID teamId) {
        final long monthsOfExperience = 5;
        final long age = 23;
        
        return givenPlayerInDb(teamId, monthsOfExperience, age);
    }

    private UUID givenPlayerInDb(final long monthsOfExperience, final long age) {
        final UUID teamId = randomUUID();
        return givenPlayerInDb(teamId, monthsOfExperience, age);
    }

    private UUID givenPlayerInDb(final UUID teamId, final long monthsOfExperience, final long age) {
        final UUID playerId = randomUUID();
        final LocalDate dateOfBirth = now(DEFAULT_ZONE_OFFSET).minusYears(age);
        final LocalDate playStart = now(DEFAULT_ZONE_OFFSET).minusMonths(monthsOfExperience);
        final CreatePlayerNoticeDto createPlayerNoticeDto = new CreatePlayerNoticeDto(playerId, teamId, dateOfBirth, playStart);

        final Player player = Player.create(createPlayerNoticeDto);
        playerRepository.save(player);

        return playerId;
    }

}