package com.dev.currencyexchange.service.impl;

import com.dev.currencyexchange.dto.CurrencyDto;
import com.dev.currencyexchange.entity.Currency;
import com.dev.currencyexchange.repository.CurrencyRepository;
import com.dev.currencyexchange.service.CurrencyRatesService;
import com.dev.currencyexchange.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyRatesService currencyRatesService;

    @Override
    public List<CurrencyDto> getAllCurrencies() {
        List<CurrencyDto> currencies = currencyRepository.findAll().stream()
                .map(currency -> new CurrencyDto(currency.getCode(), currency.getName()))
                .collect(Collectors.toList());
        if (currencies.isEmpty()) {
            throw new RuntimeException("No currencies found.");
        }
        return currencies;
    }

    @Override
    public CurrencyDto addCurrency(CurrencyDto currencyDto) {
        Currency currency = Currency.builder()
                .code(currencyDto.getCode())
                .name(currencyDto.getName())
                .build();
        Currency savedCurrency = currencyRepository.save(currency);
        if (savedCurrency.getCode().equals(currencyDto.getCode())) {
            return currencyDto;
        } else {
            throw new RuntimeException("Currency with code " + currencyDto.getCode() + " already exists.");
        }
    }

    @Override
    public Double getExchangeRatesForCurrency(String currencyCode) {
        Double exchangeRate = currencyRatesService.getExchangeRatesForCurrency(currencyCode);
        if (exchangeRate == null) {
            throw new RuntimeException("Exchange rate not found for currency: " + currencyCode);
        }
        return exchangeRate;
    }
}
