package com.dev.currencyexchange.service.impl;

import com.dev.currencyexchange.dto.ExchangeRateResponse;
import com.dev.currencyexchange.entity.Currency;
import com.dev.currencyexchange.entity.CurrencyRateLog;
import com.dev.currencyexchange.repository.CurrencyRepository;
import com.dev.currencyexchange.repository.ExchangeRateLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyRatesServiceImplTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ExchangeRateLogRepository exchangeRateLogRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyRatesServiceImpl currencyRatesService;

    @Test
    @DisplayName("Fetch exchange rates successfully when currencies exist")
    void testFetchExchangeRates_whenCurrenciesExist_returnsExchangeRates() {
        // Arrange
        Currency currency = Currency.builder().code("EUR").name("Euro").build();
        when(currencyRepository.findAll()).thenReturn(List.of(currency));

        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setRates(Map.of("EUR", 0.85));
        ResponseEntity<ExchangeRateResponse> entity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(ExchangeRateResponse.class)))
                .thenReturn(entity);

        // Act
        currencyRatesService.fetchExchangeRates();

        // Assert
        assertEquals(0.85, currencyRatesService.getExchangeRates().get("EUR"));
        verify(exchangeRateLogRepository, times(1)).save(any(CurrencyRateLog.class));
    }

    @Test
    @DisplayName("Handle empty currency list when fetching exchange rates")
    void testFetchExchangeRates_whenCurrencyListIsEmpty_returnsEmptyExchangeRates() {
        // Arrange
        when(currencyRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        currencyRatesService.fetchExchangeRates();

        // Assert
        assertTrue(currencyRatesService.getExchangeRates().isEmpty());
        verifyNoInteractions(restTemplate, exchangeRateLogRepository);
    }

    @Test
    @DisplayName("Handle API error when fetching exchange rates")
    void testFetchExchangeRates_whenApiErrorOccurs_returnsEmptyExchangeRates() {
        // Arrange
        Currency currency = Currency.builder().code("EUR").name("Euro").build();
        when(currencyRepository.findAll()).thenReturn(List.of(currency));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(ExchangeRateResponse.class)))
                .thenThrow(new RuntimeException("API error"));

        // Act
        currencyRatesService.fetchExchangeRates();

        // Assert
        assertTrue(currencyRatesService.getExchangeRates().isEmpty());
        verifyNoInteractions(exchangeRateLogRepository);
    }

    @Test
    @DisplayName("Get exchange rate for currency when rate is found")
    void testGetExchangeRatesForCurrency_whenRateIsFound_returnsExchangeRate() {
        // Arrange
        currencyRatesService.getExchangeRates().put("EUR", 0.85);

        // Act
        Double rate = currencyRatesService.getExchangeRatesForCurrency("EUR");

        // Assert
        assertEquals(0.85, rate);
    }

    @Test
    @DisplayName("Get exchange rate for currency when rate is not found")
    void testGetExchangeRatesForCurrency_whenRateIsNotFound_returnsNull() {
        // Act
        Double rate = currencyRatesService.getExchangeRatesForCurrency("GBP");

        // Assert
        assertNull(rate);
    }
}