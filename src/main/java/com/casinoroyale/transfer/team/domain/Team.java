package com.casinoroyale.transfer.team.domain;

import static com.google.common.base.Preconditions.checkState;
import static java.math.RoundingMode.HALF_UP;
import static javax.persistence.AccessType.FIELD;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.casinoroyale.team.team.dto.CreateTeamNoticeDto;
import com.casinoroyale.transfer.infrastructure.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

@Entity
@Access(FIELD)
@NoArgsConstructor(access = PRIVATE)
@ToString
class Team extends BaseEntity {

    private BigDecimal commissionRate;

    @Columns(columns = {@Column(name = "fundsCurrency"), @Column(name = "fundsAmount")})
    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmountAndCurrency")
    @Getter(PACKAGE)
    private Money funds;

    static Team create(final CreateTeamNoticeDto createTeamNoticeDto) {
        return new Team(
                createTeamNoticeDto.getTeamId(),
                createTeamNoticeDto.getCommissionRate(),
                createTeamNoticeDto.getFunds()
        );
    }

    Team(final UUID id, final BigDecimal commissionRate, final Money funds) {
        super(id);
        this.commissionRate = commissionRate;
        this.funds = funds;
    }

    void decreaseFunds(final Money fundsToDecrease) {
        funds = funds.minus(fundsToDecrease);
        checkState(funds.isPositiveOrZero(), "Team has insufficient funds");
    }

    void increaseFunds(final Money fundsToIncrease) {
        funds = funds.plus(fundsToIncrease);
    }

    Money calculateBuyerPaymentAmount(
            final Money sellerContractFee, final BiFunction<CurrencyUnit, CurrencyUnit, BigDecimal> conversionRateFunc
    ) {
        final CurrencyUnit buyerCurrency = funds.getCurrencyUnit();
        final CurrencyUnit sellerCurrency = sellerContractFee.getCurrencyUnit();

        final BigDecimal conversionRate = conversionRateFunc.apply(sellerCurrency, buyerCurrency);
        return sellerContractFee.convertedTo(buyerCurrency, conversionRate, HALF_UP);
    }

    Money calculateSellerContractFee(final BigDecimal transferFee) {
        final CurrencyUnit currency = funds.getCurrencyUnit();
        
        final BigDecimal teamCommission = transferFee.multiply(commissionRate);
        final BigDecimal contractFee = transferFee.add(teamCommission);

        return Money.of(currency, contractFee, HALF_UP);
    }

    void update(final BigDecimal newCommissionRate) {
        commissionRate = newCommissionRate;
    }
}
