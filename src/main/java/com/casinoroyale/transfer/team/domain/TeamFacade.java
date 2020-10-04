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
import com.casinoroyale.transfer.team.dto.FeePlayerTransferredNoticeDto;
import com.casinoroyale.transfer.team.dto.TeamChargedDto;
import lombok.AllArgsConstructor;
import org.joda.money.Money;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor(access = PACKAGE)
public class TeamFacade {

    private static final String FEE_AND_PLAYER_TRANSFERRED_TOPIC = "FeeAndPlayerTransferred";

    private final ExchangeRateFacade exchangeRateFacade;

    private final PlayerFacade playerFacade;

    private final KafkaTemplate<String, FeePlayerTransferredNoticeDto> kafkaTemplate;

    private final TeamRepository teamRepository;

    public TeamChargedDto chargeFee(final CreateChargeFeeDto createChargeFeeDto) {
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

        final Money sellerTeamFunds = sellerTeam.getFunds();
        final Money buyerTeamFunds = buyerTeam.getFunds();
        final FeePlayerTransferredNoticeDto feePlayerTransferredNoticeDto = new FeePlayerTransferredNoticeDto(
                sellerTeamFunds, buyerTeamFunds, sellerTeamId, buyerTeamId, playerId
        );

        kafkaTemplate.send(FEE_AND_PLAYER_TRANSFERRED_TOPIC, "", feePlayerTransferredNoticeDto);

        return new TeamChargedDto(sellerContractFee, buyerPaymentAmount);
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
