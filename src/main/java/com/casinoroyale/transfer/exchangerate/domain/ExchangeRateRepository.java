package com.casinoroyale.transfer.exchangerate.domain;

import java.util.Optional;
import java.util.UUID;

import org.joda.money.CurrencyUnit;
import org.springframework.data.jpa.repository.JpaRepository;

interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {
    
    Optional<ExchangeRate> findByCurrency(final CurrencyUnit currency);

}
