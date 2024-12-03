package com.dev.currencyexchange.repository;

import com.dev.currencyexchange.entity.CurrencyRateLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateLogRepository extends JpaRepository<CurrencyRateLog, Long> {
}
