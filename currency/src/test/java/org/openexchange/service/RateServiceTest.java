package org.openexchange.service;

import org.hibernate.ObjectNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openexchange.domain.Currency;
import org.openexchange.domain.Rate;
import org.openexchange.repository.RateRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RateServiceTest {
    @InjectMocks
    private RateServiceImpl rateService;
    @Mock
    private RateRepository rateRepository;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void testFindAll() throws Exception {
        Currency usd = new Currency("USD", "");
        Currency eur = new Currency("EUR", "");
        Currency uah = new Currency("UAH", "");

        when(rateRepository.findByIdSource(usd))
                .thenReturn(Arrays.asList(
                        new Rate(usd, eur, BigDecimal.valueOf(0.85), Date.from(Instant.now())),
                        new Rate(usd, uah, BigDecimal.valueOf(26.55), Date.from(Instant.now()))));

        List<Rate> ratesUsd = rateService.findRatesBySource(usd);
        Assert.assertEquals(2, ratesUsd.size());

        List<Rate> ratesUah = rateService.findRatesBySource(uah);
        Assert.assertEquals(0, ratesUah.size());
    }


    @Test
    public void testFindCertainRate() throws Exception {
        Currency usd = new Currency("USD", "");
        Currency uah = new Currency("UAH", "");

        Rate.RatePK id = new Rate.RatePK(usd, uah);
        when(rateRepository.findOne(id)).thenReturn(new Rate(usd, uah, BigDecimal.valueOf(26.55), Date.from(Instant.now())));

        Assert.assertNotNull(rateService.findRate(usd, uah));

    }

    @Test(expected = ObjectNotFoundException.class)
    public void testShouldFailedWhenNotFound() throws Exception {
        Currency usd = new Currency("USD", "");
        Currency eur = new Currency("EUR", "");
        Assert.assertNull(rateService.findRate(usd, eur));
    }
}
