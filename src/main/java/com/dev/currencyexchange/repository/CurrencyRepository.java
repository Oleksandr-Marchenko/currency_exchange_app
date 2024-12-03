package com.dev.currencyexchange.repository;

import com.dev.currencyexchange.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
