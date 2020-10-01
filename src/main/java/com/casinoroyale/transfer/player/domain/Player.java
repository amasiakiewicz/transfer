package com.casinoroyale.transfer.player.domain;

import static com.casinoroyale.transfer.TransferApplication.DEFAULT_ZONE_OFFSET;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.valueOf;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;
import static javax.persistence.AccessType.FIELD;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.Entity;

import com.casinoroyale.player.player.dto.CreatePlayerNoticeDto;
import com.casinoroyale.transfer.infrastructure.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Access(FIELD)
@NoArgsConstructor(access = PRIVATE)
class Player extends BaseEntity {

    @Getter(PACKAGE)
    private UUID teamId;

    private LocalDate dateOfBirth;

    private LocalDate playStart;

    static Player create(final CreatePlayerNoticeDto createPlayerNoticeDto) {
        return new Player(
                createPlayerNoticeDto.getPlayerId(),
                createPlayerNoticeDto.getTeamId(),
                createPlayerNoticeDto.getDateOfBirth(),
                createPlayerNoticeDto.getPlayStart()
        );
    }

    Player(final UUID id, final UUID teamId, final LocalDate dateOfBirth, final LocalDate playStart) {
        super(id);
        this.teamId = teamId;
        this.dateOfBirth = dateOfBirth;
        this.playStart = playStart;
    }

    BigDecimal calculateTransferFee() {
        final LocalDate now = LocalDate.now(DEFAULT_ZONE_OFFSET);

        final BigDecimal monthsOfExperience = valueOf(MONTHS.between(playStart, now));
        final BigDecimal age = valueOf(YEARS.between(dateOfBirth, now));

        return monthsOfExperience
                .multiply(valueOf(100_000))
                .divide(age, 2, ROUND_HALF_UP);
    }

    void update(final UUID newTeamId) {
        teamId = newTeamId;
    }
}
