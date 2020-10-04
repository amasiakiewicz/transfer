package com.casinoroyale.transfer.team.dto;

import com.casinoroyale.transfer.infrastructure.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Value;
import org.joda.money.Money;

@Value
public class TeamChargedDto {

    @JsonSerialize(using = MoneySerializer.class)
    Money sellerContractFee;

    @JsonSerialize(using = MoneySerializer.class)
    Money buyerPaymentAmount;

}
