package com.casinoroyale.transfer.exchangerate.domain;

import static java.math.RoundingMode.HALF_UP;
import static javax.persistence.AccessType.FIELD;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import javax.persistence.Access;
import javax.persistence.Entity;

import com.casinoroyale.exchangerate.exchangerate.dto.UpdateExchangeRateNoticeDto;
import com.casinoroyale.transfer.infrastructure.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.joda.money.CurrencyUnit;

@Entity
@Access(FIELD)
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
class ExchangeRate extends BaseEntity {

    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentCurrencyUnit")
    private CurrencyUnit currency;

    @Getter(PACKAGE)
    private BigDecimal rate;

    private OffsetDateTime date;

    public static ExchangeRate create(final CurrencyUnit currency, final UpdateExchangeRateNoticeDto updateExchangeRateNoticeDto) {
        return new ExchangeRate(
                currency,
                updateExchangeRateNoticeDto.getRate(),
                updateExchangeRateNoticeDto.getDate()
        );
    }

    BigDecimal calculateConversionRate(final ExchangeRate fromExchangeRate) {
        if (currency.equals(fromExchangeRate.currency)) {
            return BigDecimal.ONE;
        }

        return rate.divide(fromExchangeRate.rate, HALF_UP);
    }

    void update(final UpdateExchangeRateNoticeDto updateExchangeRateNoticeDto) {
        date = updateExchangeRateNoticeDto.getDate();
        rate = updateExchangeRateNoticeDto.getRate();
    }
}
