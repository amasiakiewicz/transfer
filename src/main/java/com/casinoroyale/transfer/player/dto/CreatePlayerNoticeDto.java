package com.casinoroyale.transfer.player.dto;

import static com.casinoroyale.transfer.TransferApplication.DEFAULT_ZONE_OFFSET;
import static com.google.common.base.Preconditions.checkArgument;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Value;

@Value
public class CreatePlayerNoticeDto {

    UUID playerId;

    UUID teamId;

    LocalDate dateOfBirth;

    LocalDate playStart;

    public UUID getPlayerId() {
        checkArgument(playerId != null);

        return playerId;
    }

    public UUID getTeamId() {
        checkArgument(teamId != null);
        
        return teamId;
    }

    public LocalDate getDateOfBirth() {
        final LocalDate now = LocalDate.now(DEFAULT_ZONE_OFFSET);
        checkArgument(dateOfBirth != null && dateOfBirth.isBefore(now));
        
        return dateOfBirth;
    }

    public LocalDate getPlayStart() {
        final LocalDate now = LocalDate.now(DEFAULT_ZONE_OFFSET);
        checkArgument(playStart != null && !playStart.isAfter(now));

        return playStart;
    }
    
}
