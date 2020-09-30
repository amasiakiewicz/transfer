package com.casinoroyale.transfer.team.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(makeFinal = false)
@Setter
@Getter
public class CreateChargeFeeDto {
    
    @NotNull(message = "playerId.required")
    UUID playerId;

    @NotNull(message = "buyerTeamId.required")
    UUID buyerTeamId;

}
