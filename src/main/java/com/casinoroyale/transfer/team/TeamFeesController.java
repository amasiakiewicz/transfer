package com.casinoroyale.transfer.team;

import javax.validation.Valid;

import com.casinoroyale.transfer.team.domain.TeamFacade;
import com.casinoroyale.transfer.team.dto.CreateChargeFeeDto;
import com.casinoroyale.transfer.team.dto.TeamChargedDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teamfees")
class TeamFeesController {
    
    private final TeamFacade teamFacade;

    TeamFeesController(final TeamFacade teamFacade) {
        this.teamFacade = teamFacade;
    }

    @PostMapping
    TeamChargedDto chargeFee(@Valid @RequestBody final CreateChargeFeeDto createChargeFeeDto) {
        return teamFacade.chargeFee(createChargeFeeDto);
    }

}
