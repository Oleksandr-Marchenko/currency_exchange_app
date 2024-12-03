package com.dev.currencyexchange.controller;

import com.dev.currencyexchange.dto.CurrencyDto;
import com.dev.currencyexchange.service.CurrencyService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CurrencyController.class)
class CurrencyControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CurrencyService currencyService;

    @Test
    @DisplayName("Currency can be added")
    void testAddCurrency_whenValidCurrencyDetailsProvided_returnsCreatedCurrencyDetails() throws Exception {
        // Arrange
        CurrencyDto currencyDto = new CurrencyDto("USD", "US Dollar");

        when(currencyService.addCurrency(any(CurrencyDto.class))).thenReturn(currencyDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(currencyDto));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        CurrencyDto createdCurrency = new ObjectMapper()
                .readValue(responseBodyAsString, CurrencyDto.class);

        // Assert
        assertEquals(currencyDto.getCode(),
                createdCurrency.getCode(), "The returned user first name is most likely incorrect");

        assertEquals(currencyDto.getName(),
                createdCurrency.getName(), "The returned user last name is incorrect");

    }

    @Test
    @DisplayName("All currencies can be retrieved")
    void testGetAllCurrencies_returnsListOfCurrencies() throws Exception {
        // Arrange
        List<CurrencyDto> currencyList = List.of(
                new CurrencyDto("USD", "US Dollar"),
                new CurrencyDto("EUR", "Euro")
        );

        when(currencyService.getAllCurrencies()).thenReturn(currencyList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/currencies")
                .accept(MediaType.APPLICATION_JSON);

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        List<CurrencyDto> returnedCurrencies = new ObjectMapper()
                .readValue(responseBodyAsString, new TypeReference<>() {
                });

        // Assert
        assertEquals(2, returnedCurrencies.size(), "The returned currency list size is incorrect");
        assertEquals("USD", returnedCurrencies.get(0).getCode(), "First currency code is incorrect");
        assertEquals("US Dollar", returnedCurrencies.get(0).getName(), "First currency name is incorrect");
    }

    @Test
    @DisplayName("Exchange rate for a valid currency can be retrieved")
    void testGetExchangeRateForCurrency_whenCurrencyExists_returnsExchangeRate() throws Exception {
        // Arrange
        String currencyCode = "USD";
        double exchangeRate = 1.23;

        when(currencyService.getExchangeRatesForCurrency(currencyCode)).thenReturn(exchangeRate);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/currencies/{currencyCode}", currencyCode)
                .accept(MediaType.APPLICATION_JSON);

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        Double returnedExchangeRate = new ObjectMapper().readValue(responseBodyAsString, Double.class);

        // Assert
        assertEquals(exchangeRate, returnedExchangeRate, "The returned exchange rate is incorrect");
    }

    @Test
    @DisplayName("Exchange rate for a non-existent currency returns 404")
    void testGetExchangeRateForCurrency_whenCurrencyDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        String currencyCode = "ABC";

        when(currencyService.getExchangeRatesForCurrency(currencyCode)).thenThrow(new RuntimeException("Currency not found"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/currencies/{currencyCode}", currencyCode)
                .accept(MediaType.APPLICATION_JSON);

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        assertEquals(404, mvcResult.getResponse().getStatus(), "Expected HTTP status 404 for non-existent currency");

    }
}