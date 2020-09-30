package com.casinoroyale.transfer.exchangerate.dto;

import static com.google.common.base.Preconditions.checkArgument;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Value;

@Value
public class UpdateExchangeRateDto {

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
