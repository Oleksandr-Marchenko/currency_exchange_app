package com.dev.currencyexchange.service.impl;

import com.dev.currencyexchange.dto.CurrencyDto;
import com.dev.currencyexchange.entity.Currency;
import com.dev.currencyexchange.repository.CurrencyRepository;
import com.dev.currencyexchange.service.CurrencyRatesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {
    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CurrencyRatesService currencyRatesService;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Test
    @DisplayName("Get all currencies successfully")
    void testGetAllCurrencies_whenCurrenciesExist_returnsListOfCurrencies() {
        // Arrange
        Currency currency = Currency.builder().code("USD").name("Dollar").build();
        when(currencyRepository.findAll()).thenReturn(List.of(currency));

        // Act
        List<CurrencyDto> currencies = currencyService.getAllCurrencies();

        // Assert
        assertNotNull(currencies);
        assertEquals(1, currencies.size());
        assertEquals("USD", currencies.get(0).getCode());
        verify(currencyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("No currencies found")
    void testGetAllCurrencies_whenNoCurrenciesFound_throwsException() {
        // Arrange
        when(currencyRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, currencyService::getAllCurrencies);
        assertEquals("No currencies found.", exception.getMessage());
        verify(currencyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Add currency successfully")
    void testAddCurrency_whenCurrencyDoesNotExist_addsCurrency() {
        // Arrange
        CurrencyDto currencyDto = new CurrencyDto("USD", "US Dollar");
        Currency currency = Currency.builder().code("USD").name("US Dollar").build();
        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        // Act
        CurrencyDto result = currencyService.addCurrency(currencyDto);

        // Assert
        assertNotNull(result);
        assertEquals("USD", result.getCode());
        assertEquals("US Dollar", result.getName());
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

    @Test
    @DisplayName("Currency already exists")
    void testAddCurrency_whenCurrencyAlreadyExists_throwsException() {
        // Arrange
        CurrencyDto currencyDto = new CurrencyDto("USD", "US Dollar");
        Currency currency = Currency.builder().code("USD").name("US Dollar").build();
        when(currencyRepository.save(any(Currency.class)))
                .thenThrow(new RuntimeException("Currency with code USD already exists."));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> currencyService.addCurrency(currencyDto));
        assertTrue(exception.getMessage().contains("Currency with code USD already exists."));
    }

    @Test
    @DisplayName("Get exchange rate for currency successfully")
    void testGetExchangeRatesForCurrency_whenCurrencyFound_returnsRate() {
        // Arrange
        String currencyCode = "USD";
        double exchangeRate = 1.0;
        when(currencyRatesService.getExchangeRatesForCurrency(currencyCode)).thenReturn(exchangeRate);

        // Act
        Double result = currencyService.getExchangeRatesForCurrency(currencyCode);

        // Assert
        assertNotNull(result);
        assertEquals(exchangeRate, result);
        verify(currencyRatesService, times(1)).getExchangeRatesForCurrency(currencyCode);
    }

    @Test
    @DisplayName("Exchange rate not found for currency")
    void testGetExchangeRatesForCurrency_whenCurrencyNotFound_throwsException() {
        // Arrange
        String currencyCode = "USD";
        when(currencyRatesService.getExchangeRatesForCurrency(currencyCode)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> currencyService.getExchangeRatesForCurrency(currencyCode));
        assertTrue(exception.getMessage().contains("Exchange rate not found for currency: USD"));
        verify(currencyRatesService, times(1)).getExchangeRatesForCurrency(currencyCode);
    }
}