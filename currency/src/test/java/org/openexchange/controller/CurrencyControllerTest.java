package org.openexchange.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.openexchange.domain.Currency;
import org.openexchange.domain.Rate;
import org.openexchange.service.CurrencyService;
import org.openexchange.service.RateService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CurrencyController.class)
@TestPropertySource(locations = "classpath:test.properties")
public class CurrencyControllerTest {
    @InjectMocks
    private CurrencyController currencyController;
    @InjectMocks
    private ErrorHandler errorHandler;
    @MockBean
    private CurrencyService currencyService;
    @MockBean
    private RateService rateService;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(currencyController)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @Test
    public void testShouldFindAllCurrencies() throws Exception {
        when(currencyService.findAll()).thenReturn(Arrays.asList(
                new Currency("EUR", "European Euro"),
                new Currency("USD", "United States Dollar")
        ));
        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code").value("EUR")).andExpect(jsonPath("$[0].description").value("European Euro"))
                .andExpect(jsonPath("$[1].code").value("USD")).andExpect(jsonPath("$[1].description").value("United States Dollar"));
    }

    @Test
    public void testShouldFindCertainCurrency() throws Exception {
        when(currencyService.findByCode("EUR")).thenReturn(new Currency("EUR", "European Euro"));
        mockMvc.perform(get("/currencies/EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("EUR"))
                .andExpect(jsonPath("$.description").value("European Euro"));
    }

    @Test
    public void testShouldFailedWhenCertainCurrencyInNotFound() throws Exception {
        mockMvc.perform(get("/currencies/EUR"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testShouldFailedWhenBothOfCurrenciesNotFound() throws Exception {
        mockMvc.perform(get("/quotes/EUR/USD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testShouldFailedWhenFirstCurrencyNotFound() throws Exception {
        when(currencyService.findByCode("USD")).thenReturn(new Currency("USD", "United States Dollar"));
        mockMvc.perform(get("/quotes/EUR/USD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testShouldFailedWhenLastCurrencyNotFound() throws Exception {
        when(currencyService.findByCode("EUR")).thenReturn(new Currency("EUR", "European Euro"));
        mockMvc.perform(get("/quotes/EUR/USD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testShouldFailedWhenRateNotFound() throws Exception {
        when(currencyService.findByCode("EUR")).thenReturn(new Currency("EUR", "European Euro"));
        when(currencyService.findByCode("USD")).thenReturn(new Currency("USD", "United States Dollar"));
        mockMvc.perform(get("/quotes/EUR/USD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testShouldFindRate() throws Exception {
        Currency eur = new Currency("EUR", "European Euro");
        Currency usd = new Currency("USD", "United States Dollar");
        when(currencyService.findByCode("EUR")).thenReturn(eur);
        when(currencyService.findByCode("USD")).thenReturn(usd);
        when(rateService.findRate(eur, usd)).thenReturn(new Rate(eur, usd, BigDecimal.valueOf(0.85), Date.from(Instant.now())));
        mockMvc.perform(get("/quotes/EUR/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0.850000));
    }
}
