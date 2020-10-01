package com.casinoroyale.transfer.player.domain;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface PlayerRepository extends JpaRepository<Player, UUID> {
    
}
