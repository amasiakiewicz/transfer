package com.casinoroyale.transfer.team.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChargeFeeDto {

    @NotNull(message = "playerId.required")
    private UUID playerId;

    @NotNull(message = "buyerTeamId.required")
    private UUID buyerTeamId;

}
