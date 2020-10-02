package com.casinoroyale.exchangerate.exchangerate.dto;

import static com.google.common.base.Preconditions.checkArgument;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateExchangeRateNoticeDto {

    BigDecimal rate;
    
    OffsetDateTime date;

    public BigDecimal getRate() {
        checkArgument(rate != null);

        return rate;
    }

    public OffsetDateTime getDate() {
        checkArgument(date != null);

        return date;
    }
    
}
