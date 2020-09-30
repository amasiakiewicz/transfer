package com.casinoroyale.transfer.team.domain;

import static com.casinoroyale.transfer.team.domain.Team.create;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static lombok.AccessLevel.PACKAGE;

import java.math.BigDecimal;
import java.util.UUID;

import com.casinoroyale.team.team.dto.CreateTeamNoticeDto;
import com.casinoroyale.transfer.exchangerate.domain.ExchangeRateFacade;
import com.casinoroyale.transfer.player.domain.PlayerFacade;
import com.casinoroyale.transfer.team.dto.CreateChargeFeeDto;
import lombok.AllArgsConstructor;
import org.joda.money.Money;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor(access = PACKAGE)
public class TeamFacade {

    private static final String PLAYER_TEAM_CHANGED_TOPIC = "PlayerTeamChanged";

    private final ExchangeRateFacade exchangeRateFacade;

    private final PlayerFacade playerFacade;

    private final KafkaTemplate<UUID, UUID> kafkaTemplate;

    private final TeamRepository teamRepository;

    public void chargeFee(final CreateChargeFeeDto createChargeFeeDto) {
        checkArgument(createChargeFeeDto != null);

        final UUID playerId = createChargeFeeDto.getPlayerId();
        
        final UUID sellerTeamId = playerFacade.getTeamId(playerId);
        final UUID buyerTeamId = createChargeFeeDto.getBuyerTeamId();
        checkState(!sellerTeamId.equals(buyerTeamId), "Buyer team should be different than players team");
        
        final Team sellerTeam = findTeam(sellerTeamId);
        final Team buyerTeam = findTeam(buyerTeamId);

        final Money sellerContractFee = calculateSellerContractFee(playerId, sellerTeam);
        final Money buyerPaymentAmount = buyerTeam.calculateBuyerPaymentAmount(
                sellerContractFee, exchangeRateFacade::calculateConversionRate
        );

        sellerTeam.increaseFunds(sellerContractFee);
        buyerTeam.decreaseFunds(buyerPaymentAmount);

        kafkaTemplate.send(PLAYER_TEAM_CHANGED_TOPIC, playerId, buyerTeamId);
    }

    public void createTeam(final CreateTeamNoticeDto createTeamNoticeDto) {
        checkArgument(createTeamNoticeDto != null);

        final Team team = create(createTeamNoticeDto);
        teamRepository.save(team);
    }

    public void updateTeam(final UUID teamId, final BigDecimal newCommissionRate) {
        checkArgument(teamId != null);
        checkArgument(newCommissionRate != null);

        final Team team = findTeam(teamId);
        team.update(newCommissionRate);
    }

    public void deleteTeam(final UUID teamId) {
        checkArgument(teamId != null);
        
        final Team team = findTeam(teamId);
        teamRepository.delete(team);
    }

    private Money calculateSellerContractFee(final UUID playerId, final Team sellerTeam) {
        final BigDecimal transferFee = playerFacade.calculateTransferFee(playerId);

        return sellerTeam.calculateSellerContractFee(transferFee);
    }

    private Team findTeam(final UUID teamId) {
        return teamRepository
                .findById(teamId)
                .<IllegalArgumentException> orElseThrow(() -> {
                    throw new IllegalArgumentException(format("Team %s does not exist", teamId));
                });
    }
}
