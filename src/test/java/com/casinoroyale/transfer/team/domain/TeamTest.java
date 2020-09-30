package com.casinoroyale.transfer.team.domain;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;

import com.casinoroyale.team.team.dto.CreateTeamNoticeDto;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;

class TeamTest {

    @Test
    void shouldCalculateBuyerPaymentAmount() {
        //given
        final CurrencyUnit buyerTeamCurrency = CurrencyUnit.of("PLN");
        final Team buyerTeam = givenTeam(buyerTeamCurrency);
        final Money sellerContractFee = Money.of(CurrencyUnit.of("EUR"), 15623.45);
        final BiFunction<CurrencyUnit, CurrencyUnit, BigDecimal> conversionRateFunc = (c1, c2) -> valueOf(4.5557);

        //when
        final Money buyerPaymentAmount = buyerTeam.calculateBuyerPaymentAmount(sellerContractFee, conversionRateFunc);

        //then
        assertThat(buyerPaymentAmount).isEqualTo(Money.of(buyerTeamCurrency, valueOf(71175.75)));
    }

    @Test
    void shouldCalculateSellerContractFee() {
        //given
        final BigDecimal transferFee = valueOf(15624.33);
        final BigDecimal commissionRate = valueOf(0.07);
        final CurrencyUnit teamCurrency = CurrencyUnit.of("PLN");
        final Team team = givenTeam(teamCurrency, commissionRate);

        //when
        final Money sellerContractFee = team.calculateSellerContractFee(transferFee);

        //then
        assertThat(sellerContractFee).isEqualTo(Money.of(teamCurrency, valueOf(16718.03)));
    }

    private Team givenTeam(final CurrencyUnit currency) {
        final BigDecimal commissionRate = valueOf(0.07);
        return givenTeam(currency, commissionRate);
    }

    private Team givenTeam(final CurrencyUnit currency, final BigDecimal commissionRate) {
        final UUID teamId = UUID.randomUUID();
        final Money funds = Money.of(currency, 123.3);

        final CreateTeamNoticeDto createTeamNoticeDto = new CreateTeamNoticeDto(teamId, commissionRate, funds);
        return Team.create(createTeamNoticeDto);
    }
}