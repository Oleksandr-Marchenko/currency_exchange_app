package com.dev.currencyexchange.service.impl;

import com.dev.currencyexchange.dto.CurrencyDto;
import com.dev.currencyexchange.entity.Currency;
import com.dev.currencyexchange.repository.CurrencyRepository;
import com.dev.currencyexchange.service.CurrencyRatesService;
import com.dev.currencyexchange.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private static final Logger LOG = LoggerFactory.getLogger(CurrencyServiceImpl.class);


    private final CurrencyRepository currencyRepository;
    private final CurrencyRatesService currencyRatesService;

    @Override
    public List<CurrencyDto> getAllCurrencies() {
        List<CurrencyDto> currencies = currencyRepository.findAll().stream()
                .map(currency -> new CurrencyDto(currency.getCode(), currency.getName()))
                .collect(Collectors.toList());
        if (currencies.isEmpty()) {
            LOG.warn("No currencies found in the database.");
            throw new RuntimeException("No currencies found.");
        }

        LOG.info("Found {} currencies.", currencies.size());
        return currencies;
    }

    @Override
    public CurrencyDto addCurrency(CurrencyDto currencyDto) {
        LOG.info("Attempting to add new currency with code: {}", currencyDto.getCode());

        Currency currency = Currency.builder()
                .code(currencyDto.getCode())
                .name(currencyDto.getName())
                .build();
        Currency savedCurrency = currencyRepository.save(currency);
        if (savedCurrency.getCode().equals(currencyDto.getCode())) {
            LOG.info("Successfully added new currency: {}", currencyDto.getCode());
            return currencyDto;
        } else {
            LOG.error("Currency with code {} already exists.", currencyDto.getCode());
            throw new RuntimeException("Currency with code " + currencyDto.getCode() + " already exists.");
        }
    }

    @Override
    public Double getExchangeRatesForCurrency(String currencyCode) {
        LOG.info("Fetching exchange rate for currency: {}", currencyCode);

        Double exchangeRate = currencyRatesService.getExchangeRatesForCurrency(currencyCode);
        if (exchangeRate == null) {
            LOG.error("Exchange rate not found for currency: {}", currencyCode);
            throw new RuntimeException("Exchange rate not found for currency: " + currencyCode);
        }

        LOG.info("Exchange rate for currency {} is {}", currencyCode, exchangeRate);
        return exchangeRate;
    }
}
