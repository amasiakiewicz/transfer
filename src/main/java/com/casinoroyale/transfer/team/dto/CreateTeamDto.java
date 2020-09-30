package com.casinoroyale.transfer.team.dto;

import static com.google.common.base.Preconditions.checkArgument;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Value;
import org.joda.money.Money;

@Value
public class CreateTeamDto {

    UUID teamId;

    BigDecimal commissionRate;

    Money funds;

    public UUID getTeamId() {
        checkArgument(teamId != null);

        return teamId;
    }

    public BigDecimal getCommissionRate() {
        checkArgument(commissionRate != null);

        return commissionRate;
    }
    
    public Money getFunds() {
        checkArgument(funds != null);

        return funds;
    }
    
}
