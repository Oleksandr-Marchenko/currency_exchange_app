package com.dev.currencyexchange.controller;

import com.dev.currencyexchange.dto.CurrencyDto;
import com.dev.currencyexchange.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    private static final Logger LOG = LoggerFactory.getLogger(CurrencyController.class);

    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<List<CurrencyDto>> getAllCurrencies() {
        List<CurrencyDto> allCurrencies = currencyService.getAllCurrencies();
        LOG.info("Fetched {} currencies from the database", allCurrencies.size());
        return ResponseEntity.ok(allCurrencies);
    }

    @PostMapping
    public ResponseEntity<CurrencyDto> addCurrency(@RequestBody CurrencyDto currencyDto) {
        CurrencyDto currency = currencyService.addCurrency(currencyDto);
        LOG.info("Created new currency: {}", currency);
        return ResponseEntity.ok(currency);
    }

    @GetMapping("/{currencyCode}")
    public ResponseEntity<Double> getExchangeRateForCurrency(@PathVariable String currencyCode) {
        Double exchangeRate = currencyService.getExchangeRatesForCurrency(currencyCode);

        if (exchangeRate == null) {
            LOG.warn("Exchange rate for currency {} not found", currencyCode);
            return ResponseEntity.notFound().build();
        }

        LOG.info("Returning exchange rate {} for currency {}", exchangeRate, currencyCode);
        return ResponseEntity.ok(exchangeRate);
    }
}
