package com.casinoroyale.transfer.team.domain;

import static com.casinoroyale.transfer.TransferApplication.DEFAULT_ZONE_OFFSET;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static java.time.OffsetDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration.builder;
import static org.joda.money.CurrencyUnit.USD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.casinoroyale.player.player.dto.CreatePlayerNoticeDto;
import com.casinoroyale.team.team.dto.CreateTeamNoticeDto;
import com.casinoroyale.transfer.exchangerate.domain.ExchangeRateFacade;
import com.casinoroyale.transfer.exchangerate.dto.UpdateExchangeRateDto;
import com.casinoroyale.transfer.player.domain.PlayerFacade;
import com.casinoroyale.transfer.team.dto.CreateChargeFeeDto;
import com.casinoroyale.transfer.team.dto.TeamChargedDto;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TeamFacadeIT {

    @Autowired //SUT
    private TeamFacade teamFacade;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ExchangeRateFacade exchangeRateFacade;

    @Autowired
    private PlayerFacade playerFacade;

    @Test
    void shouldChargeFee() {
        //given
        final CurrencyUnit usd = CurrencyUnit.of("USD");
        final Money sellerTeamFunds = Money.of(usd, 200_000);
        final double teamCommission = 0.07;
        final UUID sellerTeamId = givenTeamInDb(sellerTeamFunds, teamCommission);

        final long monthOfExperience = 15;
        final long age = 24;
        final UUID playerId = givenPlayerInDb(sellerTeamId, monthOfExperience, age);

        final CurrencyUnit pln = CurrencyUnit.of("PLN");
        final Money buyerTeamFunds = Money.of(pln, 300_000);
        final UUID buyerTeamId = givenTeamInDb(buyerTeamFunds);

        givenExchangeRateInDb(usd, pln, 3.9228);

        final CreateChargeFeeDto createChargeFeeDto = new CreateChargeFeeDto(playerId, buyerTeamId);
        final Money expectedSellerContractFee = Money.of(usd, 66_875);
        final Money expectedSellerFunds = Money.of(usd, 266_875);
        final Money expectedBuyerPaymentAmount = Money.of(pln, 262_337.25);
        final Money expectedBuyerFunds = Money.of(pln, 37662.75);

        //when
        final TeamChargedDto teamChargedDto = teamFacade.chargeFee(createChargeFeeDto);

        //then
        assertThat(teamChargedDto)
                .isEqualTo(expectedTeamChargedDto(expectedSellerContractFee, expectedSellerFunds, expectedBuyerPaymentAmount, expectedBuyerFunds))
                .satisfies((c) -> fundsAfterCharge(sellerTeamId, expectedSellerFunds, buyerTeamId, expectedBuyerFunds));
    }

    @Test
    void shouldNotChargeFeeWhenBuyerTeamIsTheSameAsPlayersTeam() {
        //given
        final CurrencyUnit usd = CurrencyUnit.of("USD");
        final Money sellerTeamFunds = Money.of(usd, 200_000);
        final double teamCommission = 0.07;
        final UUID sellerTeamId = givenTeamInDb(sellerTeamFunds, teamCommission);

        final long monthOfExperience = 15;
        final long age = 24;
        final UUID playerId = givenPlayerInDb(sellerTeamId, monthOfExperience, age);

        final CreateChargeFeeDto createChargeFeeDto = new CreateChargeFeeDto(playerId, sellerTeamId);

        //when
        final Throwable exceptionThrown = catchThrowable(() -> teamFacade.chargeFee(createChargeFeeDto));

        //then
        assertThat(exceptionThrown).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldCreateTeam() {
        //given
        final UUID teamId = randomUUID();
        final BigDecimal commissionRate = valueOf(0.05);
        final Money funds = Money.of(USD, 123.45);
        final CreateTeamNoticeDto createTeamNoticeDto = new CreateTeamNoticeDto(teamId, commissionRate, funds);

        //when
        teamFacade.createTeam(createTeamNoticeDto);

        //then
        assertThat(existingTeamInDb(teamId))
                .usingRecursiveComparison(builder().withIgnoredFields("version", "createdDateTime").build())
                .isEqualTo(expectedTeam(teamId, commissionRate, funds));
    }

    @Test
    void shouldUpdateTeamOnlyWithCommissionRate() {
        //given
        final BigDecimal oldCommissionRate = valueOf(0.04);
        final Money funds = Money.of(USD, 123.45);
        final UUID teamId = givenTeamInDb(funds, oldCommissionRate);
        final BigDecimal newCommissionRate = valueOf(0.07);

        //when
        teamFacade.updateTeam(teamId, newCommissionRate);

        //then
        assertThat(existingTeamInDb(teamId))
                .usingRecursiveComparison(builder().withIgnoredFields("version", "createdDateTime").build())
                .isEqualTo(expectedTeam(teamId, newCommissionRate, funds));
    }

    @Test
    void shouldDeleteTeam() {
        //given
        final UUID teamId = givenTeamInDb();

        //when
        teamFacade.deleteTeam(teamId);

        //then
        assertThat(teamId).satisfies(this::doesntExistInDb);
    }

    private void doesntExistInDb(final UUID teamId) {
        final boolean exists = teamRepository.existsById(teamId);
        assertThat(exists).isFalse();
    }

    private UUID givenTeamInDb() {
        final Money funds = Money.of(USD, 300);
        return givenTeamInDb(funds);
    }

    private Team expectedTeam(final UUID teamId, final BigDecimal commissionRate, final Money funds) {
        final BigDecimal commissionRateScaled = commissionRate.setScale(4, HALF_UP);
        return new Team(teamId, commissionRateScaled, funds);
    }

    private Team existingTeamInDb(final UUID teamId) {
        return teamRepository
                .findById(teamId)
                .orElseThrow(IllegalStateException::new);
    }

    private TeamChargedDto expectedTeamChargedDto(
            final Money expectedSellerContractFee, final Money expectedSellerFunds,
            final Money expectedBuyerPaymentAmount, final Money expectedBuyerFunds
    ) {
        return new TeamChargedDto(expectedSellerContractFee, expectedSellerFunds, expectedBuyerPaymentAmount, expectedBuyerFunds);
    }

    private void fundsAfterCharge(final UUID sellerTeamId, final Money expectedSellerFunds, final UUID buyerTeamId, final Money expectedBuyerFunds) {
        final Team sellerTeam = teamRepository
                .findById(sellerTeamId)
                .orElseThrow(IllegalStateException::new);
        assertThat(sellerTeam.getFunds()).isEqualTo(expectedSellerFunds);

        final Team buyerTeam = teamRepository
                .findById(buyerTeamId)
                .orElseThrow(IllegalStateException::new);
        assertThat(buyerTeam.getFunds()).isEqualTo(expectedBuyerFunds);
    }

    private void givenExchangeRateInDb(final CurrencyUnit fromCurrency, final CurrencyUnit toCurrency, final double rate) {
        final OffsetDateTime now = now(DEFAULT_ZONE_OFFSET);
        final UpdateExchangeRateDto updateFromExchangeRateDto = new UpdateExchangeRateDto(valueOf(1.0), now);
        exchangeRateFacade.createOrUpdateExchangeRate(fromCurrency, updateFromExchangeRateDto);

        final UpdateExchangeRateDto updateToExchangeRateDto = new UpdateExchangeRateDto(valueOf(rate), now);
        exchangeRateFacade.createOrUpdateExchangeRate(toCurrency, updateToExchangeRateDto);
    }

    private UUID givenTeamInDb(final Money funds) {
        return givenTeamInDb(funds, 0.03);
    }

    private UUID givenTeamInDb(final Money funds, final double teamCommission) {
        return givenTeamInDb(funds, valueOf(teamCommission));
    }

    private UUID givenTeamInDb(final Money funds, final BigDecimal teamCommission) {
        final UUID teamId = randomUUID();
        final CreateTeamNoticeDto createTeamNoticeDto = new CreateTeamNoticeDto(teamId, teamCommission, funds);
        teamFacade.createTeam(createTeamNoticeDto);

        return teamId;
    }

    private UUID givenPlayerInDb(final UUID teamId, final long monthOfExperience, final long age) {
        final UUID playerId = randomUUID();
        final LocalDate now = LocalDate.now(DEFAULT_ZONE_OFFSET);
        final LocalDate dateOfBirth = now.minusYears(age);
        final LocalDate playStart = now.minusMonths(monthOfExperience);

        final CreatePlayerNoticeDto createPlayerNoticeDto = new CreatePlayerNoticeDto(playerId, teamId, dateOfBirth, playStart);
        playerFacade.createPlayer(createPlayerNoticeDto);

        return playerId;
    }

}