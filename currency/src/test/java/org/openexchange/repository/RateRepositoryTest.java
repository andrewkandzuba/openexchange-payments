package org.openexchange.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openexchange.domain.Currency;
import org.openexchange.domain.Rate;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:test.properties")
public class RateRepositoryTest {
    @Inject
    private RateRepository rateRepository;

    @Inject
    private CurrencyRepository currencyRepository;

    @Test
    public void testShouldAddNewRate() throws Exception {
        Currency eur = new Currency("EUR", "European Euro");
        Currency usd = new Currency("USD", "United States Dollar");
        currencyRepository.save(eur);
        currencyRepository.save(usd);
        rateRepository.save(new Rate(eur, usd, BigDecimal.valueOf(1.8), Date.from(Instant.now())));
        List<Rate> rate = rateRepository.findAll();
        Assert.assertEquals(1, rate.size());

        Rate found = rateRepository.findOne(new Rate.RatePK(eur, usd));
        Assert.assertNotNull(found);
        Assert.assertEquals("EUR", found.getSource().getCode());
        Assert.assertEquals("European Euro", found.getSource().getDescription());
        Assert.assertEquals("USD", found.getTarget().getCode());
        Assert.assertEquals("United States Dollar", found.getTarget().getDescription());
        Assert.assertEquals("1.800000", found.getQuote().toPlainString());
        Assert.assertNotNull(found.getTimestamp());
    }

    @Test
    public void testShouldOverride() throws Exception {
        Currency eur = new Currency("EUR", "European Euro");
        Currency usd = new Currency("USD", "United States Dollar");

        currencyRepository.save(eur);
        currencyRepository.save(usd);
        rateRepository.save(new Rate(eur, usd, BigDecimal.valueOf(0.8000), Date.from(Instant.now())));
        rateRepository.save(new Rate(eur, usd, BigDecimal.valueOf(0.8777), Date.from(Instant.now())));

        List<Rate> rate = rateRepository.findAll();
        Assert.assertEquals(1, rate.size());
        Assert.assertEquals("0.877700", rate.get(0).getQuote().toPlainString());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testShouldFailedOnForeignKey() throws Exception {
        Currency eur = new Currency("EUR", "European Euro");
        Currency uah = new Currency("UAH", "Ukrainian Hryvna");
        currencyRepository.save(eur);
        rateRepository.save(new Rate(eur, uah, BigDecimal.valueOf(0.8), Date.from(Instant.now())));
    }

    @Test
    public void testShouldDeleteCascade() throws Exception {
        Currency eur = new Currency("EUR", "European Euro");
        Currency uah = new Currency("UAH", "Ukrainian Hryvna");
        Currency usd = new Currency("USD", "United States Dollar");

        currencyRepository.save(eur);
        currencyRepository.save(usd);
        currencyRepository.save(uah);

        rateRepository.save(new Rate(eur, uah, BigDecimal.valueOf(30.25), Date.from(Instant.now())));
        rateRepository.save(new Rate(eur, usd, BigDecimal.valueOf(1.15), Date.from(Instant.now())));

        List<Rate> rates = rateRepository.findAll();
        Assert.assertEquals(2, rates.size());

        currencyRepository.delete("EUR");
        rates = rateRepository.findAll();
        Assert.assertEquals(0, rates.size());
    }

    @Test
    public void testShouldFindByIdSource() throws Exception {
        Currency eur = new Currency("EUR", "European Euro");
        Currency uah = new Currency("UAH", "Ukrainian Hryvna");
        Currency usd = new Currency("USD", "United States Dollar");

        currencyRepository.save(eur);
        currencyRepository.save(usd);
        currencyRepository.save(uah);

        rateRepository.save(new Rate(eur, uah, BigDecimal.valueOf(30.25), Date.from(Instant.now())));
        rateRepository.save(new Rate(eur, usd, BigDecimal.valueOf(1.15), Date.from(Instant.now())));
        rateRepository.save(new Rate(usd, uah, BigDecimal.valueOf(26.55), Date.from(Instant.now())));

        List<Rate> rates = rateRepository.findByIdSource(eur);
        Assert.assertEquals(2, rates.size());

        Assert.assertEquals(eur.getCode(), rates.get(0).getSource().getCode());
        Assert.assertEquals(eur.getCode(), rates.get(1).getSource().getCode());
    }

    @Test
    public void testShouldFindByIdSourceAndIdTarget() throws Exception {
        Currency eur = new Currency("EUR", "European Euro");
        Currency uah = new Currency("UAH", "Ukrainian Hryvna");
        Currency usd = new Currency("USD", "United States Dollar");

        currencyRepository.save(eur);
        currencyRepository.save(usd);
        currencyRepository.save(uah);

        rateRepository.save(new Rate(eur, uah, BigDecimal.valueOf(30.25), Date.from(Instant.now())));
        rateRepository.save(new Rate(eur, usd, BigDecimal.valueOf(1.15), Date.from(Instant.now())));
        rateRepository.save(new Rate(usd, uah, BigDecimal.valueOf(26.55), Date.from(Instant.now())));

        Rate rateEurToUsd = rateRepository.findOne(new Rate.RatePK(eur, uah));
        Assert.assertEquals(eur.getCode(), rateEurToUsd.getSource().getCode());
        Assert.assertEquals(uah.getCode(), rateEurToUsd.getTarget().getCode());

        Rate rateUsdToEur = rateRepository.findOne(new Rate.RatePK(usd, eur));
        Assert.assertNull(rateUsdToEur);
    }
}

