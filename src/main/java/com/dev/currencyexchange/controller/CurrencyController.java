package com.dev.currencyexchange.controller;

import com.dev.currencyexchange.dto.CurrencyDto;
import com.dev.currencyexchange.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    private static final Logger LOG = LoggerFactory.getLogger(CurrencyController.class);

    private final CurrencyService currencyService;

    @Operation(summary = "Get a list of currencies used in the project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CurrencyDto.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping
    public ResponseEntity<List<CurrencyDto>> getAllCurrencies() {
        List<CurrencyDto> allCurrencies = currencyService.getAllCurrencies();
        LOG.info("Fetched {} currencies from the database", allCurrencies.size());
        return ResponseEntity.ok(allCurrencies);
    }

    @Operation(summary = "Add new currency for getting exchange rates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CurrencyDto.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping
    public ResponseEntity<CurrencyDto> addCurrency(@RequestBody CurrencyDto currencyDto) {
        CurrencyDto currency = currencyService.addCurrency(currencyDto);
        LOG.info("Created new currency: {}", currency);
        return ResponseEntity.ok(currency);
    }

    @Operation(summary = "Get exchange rates for a currency.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(type = "number", format = "double", example = "1.23")
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/{currencyCode}")
    public ResponseEntity<Double> getExchangeRateForCurrency(@PathVariable String currencyCode) {
        try {
            Double exchangeRate = currencyService.getExchangeRatesForCurrency(currencyCode);
            LOG.info("Returning exchange rate {} for currency {}", exchangeRate, currencyCode);
            return ResponseEntity.ok(exchangeRate);
        } catch (RuntimeException e) {
            LOG.warn("Exchange rate for currency {} not found", currencyCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
