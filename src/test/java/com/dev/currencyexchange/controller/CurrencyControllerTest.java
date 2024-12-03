package com.dev.currencyexchange.controller;

import com.dev.currencyexchange.dto.CurrencyDto;
import com.dev.currencyexchange.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyService;

    private CurrencyController currencyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyController = new CurrencyController(currencyService);
    }

    @Test
    @DisplayName("Get all existing currencies")
    void testGetAllCurrencies_whenCurrenciesExist_returnsListOfCurrencies() {
        // Arrange
        List<CurrencyDto> mockCurrencies = Arrays.asList(
                new CurrencyDto("USD", "US Dollar"),
                new CurrencyDto("EUR", "Euro")
        );
        when(currencyService.getAllCurrencies()).thenReturn(mockCurrencies);

        // Act
        ResponseEntity<List<CurrencyDto>> response = currencyController.getAllCurrencies();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(currencyService, times(1)).getAllCurrencies();
    }

    @Test
    @DisplayName("No currencies found")
    void testGetAllCurrencies_whenNoCurrenciesFound_throwsException() {
        // Arrange
        when(currencyService.getAllCurrencies()).thenThrow(new RuntimeException("No currencies found."));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            currencyController.getAllCurrencies();
        });
        assertEquals("No currencies found.", exception.getMessage());
        verify(currencyService, times(1)).getAllCurrencies();
    }

    @Test
    @DisplayName("Currency can be added")
    void testAddCurrency_whenCurrencyNotExists_returnsCreatedCurrency() {
        // Arrange
        CurrencyDto inputCurrency = new CurrencyDto("USD", "US Dollar");
        when(currencyService.addCurrency(any(CurrencyDto.class))).thenReturn(inputCurrency);

        // Act
        ResponseEntity<CurrencyDto> response = currencyController.addCurrency(inputCurrency);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("USD", response.getBody().getCode());
        assertEquals("US Dollar", response.getBody().getName());
        verify(currencyService, times(1)).addCurrency(any(CurrencyDto.class));
    }

    @Test
    @DisplayName("Add existing currency")
    void testAddCurrency_whenCurrencyExists_throwsException() {
        // Arrange
        CurrencyDto inputCurrency = new CurrencyDto("USD", "US Dollar");
        when(currencyService.addCurrency(any(CurrencyDto.class))).thenThrow(new RuntimeException("Currency already exists."));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            currencyController.addCurrency(inputCurrency);
        });
        assertEquals("Currency already exists.", exception.getMessage());
        verify(currencyService, times(1)).addCurrency(any(CurrencyDto.class));
    }

    @Test
    @DisplayName("Get exchange rate for currency")
    void testGetExchangeRateForCurrency_whenExchangeRateExists_returnsExchangeRate() {
        // Arrange
        String currencyCode = "USD";
        Double mockExchangeRate = 1.23;
        when(currencyService.getExchangeRatesForCurrency(currencyCode)).thenReturn(mockExchangeRate);

        // Act
        ResponseEntity<Double> response = currencyController.getExchangeRateForCurrency(currencyCode);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(mockExchangeRate, response.getBody());
        verify(currencyService, times(1)).getExchangeRatesForCurrency(currencyCode);
    }

    @Test
    @DisplayName("Get not existing exchange rate for currency")
    void testGetExchangeRateForCurrency_whenNotFound_returnsNotFound() {
        // Arrange
        String currencyCode = "XYZ";
        when(currencyService.getExchangeRatesForCurrency(currencyCode)).thenThrow(new RuntimeException("Currency not found"));

        // Act
        ResponseEntity<Double> response = currencyController.getExchangeRateForCurrency(currencyCode);

        // Assert
        assertEquals(404, response.getStatusCode().value());
        assertFalse(response.hasBody());
        verify(currencyService, times(1)).getExchangeRatesForCurrency(currencyCode);
    }
}
