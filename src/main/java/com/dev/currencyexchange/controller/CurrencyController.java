package com.dev.currencyexchange.controller;

import com.dev.currencyexchange.dto.CurrencyDto;
import com.dev.currencyexchange.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<List<CurrencyDto>> getAllCurrencies() {
        List<CurrencyDto> allCurrencies = currencyService.getAllCurrencies();
        return ResponseEntity.ok(allCurrencies);
    }

    @PostMapping
    public ResponseEntity<CurrencyDto> addCurrency(@RequestBody CurrencyDto currencyDto) {
        CurrencyDto currency = currencyService.addCurrency(currencyDto);
        return ResponseEntity.ok(currency);
    }

    @GetMapping("/{currencyCode}")
    public ResponseEntity<Double> getExchangeRateForCurrency(@PathVariable String currencyCode) {
        Double exchangeRate = currencyService.getExchangeRatesForCurrency(currencyCode);

        if (exchangeRate == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(exchangeRate);
    }
}
