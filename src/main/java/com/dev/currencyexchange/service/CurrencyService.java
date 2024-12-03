package com.dev.currencyexchange.service;

import com.dev.currencyexchange.dto.CurrencyDto;

import java.util.List;

public interface CurrencyService {

    List<CurrencyDto> getAllCurrencies();

    CurrencyDto addCurrency(CurrencyDto currencyDto);

    Double getExchangeRatesForCurrency(String currencyCode);
}
