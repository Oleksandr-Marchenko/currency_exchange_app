package com.dev.currencyexchange.service.impl;

import com.dev.currencyexchange.dto.ExchangeRateResponse;
import com.dev.currencyexchange.entity.Currency;
import com.dev.currencyexchange.entity.CurrencyRateLog;
import com.dev.currencyexchange.repository.CurrencyRepository;
import com.dev.currencyexchange.repository.ExchangeRateLogRepository;
import com.dev.currencyexchange.service.CurrencyRatesService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyRatesServiceImpl implements CurrencyRatesService {

    @Getter
    private final Map<String, Double> exchangeRates = new ConcurrentHashMap<>();
    private final CurrencyRepository currencyRepository;
    private final ExchangeRateLogRepository exchangeRateLogRepository;
    private final RestTemplate restTemplate;

    @Value("${base.currency}")
    private String baseCurrency;

    @Value("${exchange.api.url}")
    private String exchangeRatesApiUrl;

    @Value("${api.key}")
    private String apiKey;

    @Scheduled(fixedRate = 3600000)
    public void fetchExchangeRates() {

        List<Currency> currencies = currencyRepository.findAll();
        if (currencies.isEmpty()) {
            return;
        }

        String symbols = currencies.stream().map(Currency::getCode).collect(Collectors.joining(","));

        String url = String.format("%s?access_key=%s&base=%s&symbols=%s", exchangeRatesApiUrl, apiKey, baseCurrency, symbols);

        try {
            ResponseEntity<ExchangeRateResponse> response = restTemplate.exchange(url, HttpMethod.GET,
                    null, ExchangeRateResponse.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ExchangeRateResponse exchangeRateResponse = response.getBody();
                if (exchangeRateResponse != null && exchangeRateResponse.getRates() != null) {
                    exchangeRates.clear();
                    exchangeRates.putAll(exchangeRateResponse.getRates());
                    exchangeRateLogRepository.save(CurrencyRateLog.builder()
                            .baseCurrency(baseCurrency)
                            .rates(exchangeRateResponse.getRates())
                            .build());
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching exchange rates");
        }
    }

    @Override
    public Double getExchangeRatesForCurrency(String currencyCode) {
        return exchangeRates.get(currencyCode);
    }
}
