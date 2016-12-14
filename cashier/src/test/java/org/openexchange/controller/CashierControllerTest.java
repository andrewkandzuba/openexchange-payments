package org.openexchange.controller;

import feign.FeignException;
import feign.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.openexchange.client.CurrencyClient;
import org.openexchange.config.CashierConfiguration;
import org.openexchange.controllers.CashierController;
import org.openexchange.controllers.ErrorHandler;
import org.openexchange.service.CashierService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CashierController.class)
@TestPropertySource(locations = "classpath:test.properties")
public class CashierControllerTest {
    @InjectMocks
    private CashierController cashierController;
    @InjectMocks
    private ErrorHandler errorHandler;
    @MockBean
    private CashierService cashierService;
    @MockBean
    private CashierConfiguration cashierConfiguration;
    @MockBean
    private CurrencyClient currencyClient;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(cashierController)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @Test
    public void testShouldExchangeAmount() throws Exception {
        when(cashierService.exchange("USD", "UAH", 10.0)).thenReturn(BigDecimal.valueOf(260.0));
        mockMvc.perform(get("/exchange/USD/UAH/10.0")).andExpect(status().isOk()).andExpect(jsonPath("$").value(260.000000));
    }

    @Test(expected = NestedServletException.class)
    public void testShouldFailed() throws Exception {
        Map<String, Collection<String>> headers = Collections.emptyMap();
        when(cashierService.exchange("USD", "UAH", 10.0))
                .thenThrow(FeignException.errorStatus(
                        "exchange",
                        Response.create(
                                400, "A rate does not exist for the combination of the source and the target currencies",
                                headers, new byte[]{})));
        mockMvc.perform(get("/exchange/USD/UAH/10.0"));
    }
}
