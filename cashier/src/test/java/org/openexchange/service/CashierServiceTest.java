package org.openexchange.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openexchange.client.CurrencyClient;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CashierServiceTest {
    @InjectMocks
    private CashierServiceImpl currencyService;
    @Mock
    private CurrencyClient currencyClient;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void testExchange() throws Exception {
        when(currencyClient.findQuote("EUR", "UAH")).thenReturn(BigDecimal.valueOf(30.0));
        BigDecimal amount = currencyService.exchange("EUR", "UAH", 10.0);
        Assert.assertTrue(amount.compareTo(BigDecimal.valueOf(300.0)) == 0);
    }

    //@Test
    public void testFailedExchange() throws Exception {
        BigDecimal amount = currencyService.exchange("EUR", "UAH", 10.0);
        Assert.assertTrue(amount.compareTo(BigDecimal.valueOf(300.0)) == 0);
    }
}
