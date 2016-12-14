package org.openexchange.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openexchange.domain.Currency;
import org.openexchange.repository.CurrencyRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CurrencyServiceTest {
    @InjectMocks
    private CurrencyServiceImpl currencyService;
    @Mock
    private CurrencyRepository currencyRepository;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void testFindAll() throws Exception {
        when(currencyRepository.findAll(Mockito.any(org.springframework.data.domain.Sort.class)))
                .thenReturn(Arrays.asList(
                                new Currency("USD", "United States Dollar"),
                                new Currency("EUR", "European euro")));
        List<Currency> currencies = currencyService.findAll();
        final boolean[] found = {false};
        currencies.forEach(currency -> {
            if (currency.getCode().equalsIgnoreCase("USD")) {
                found[0] = true;
            }
        });
        Assert.assertTrue(found[0]);
    }

    @Test
    public void testFindOne() throws Exception {
        when(currencyRepository.findOne("USD"))
                .then(invocationOnMock -> new Currency("USD", "United States Dollar"));
        assertNotNull(currencyService.findByCode("USD"));
        Assert.assertEquals(null, currencyService.findByCode("UAH"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateShouldFailedWhenCodeIsEmpty() throws Exception {
        currencyService.create(null, null);
        currencyService.create("USD", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateShouldFailedWhenDescriptionIsEmpty() throws Exception {
        currencyService.create("USD", null);
    }

    @Test
    public void testShouldCreate() throws Exception {
        Currency usd = currencyService.create("USD", "United States Dollar");
        verify(currencyRepository, times(1)).save(usd);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateShouldFailedWhenCodeIsEmpty() throws Exception {
        Currency currency = new Currency(null, null);
        currencyService.saveChanges(currency);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateShouldFailedWhenDescriptionIsEmpty() throws Exception {
        Currency currency = new Currency("USD", null);
        currencyService.saveChanges(currency);
    }

    @Test
    public void testShouldUpdate() throws Exception {
        when(currencyRepository.findOne("USD"))
                .then(invocationOnMock -> new Currency("USD", "United States Dollar"));
        Currency currency = new Currency("USD", "United States Dollar - updated");
        currencyService.saveChanges(currency);
        verify(currencyRepository, times(1)).save(currency);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldFailedNotFound() throws Exception {
        Currency currency = new Currency("USD", "United States Dollar - updated");
        currencyService.saveChanges(currency);
        verify(currencyRepository, times(1)).save(currency);
    }
}
