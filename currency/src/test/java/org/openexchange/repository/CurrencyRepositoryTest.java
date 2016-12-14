package org.openexchange.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openexchange.domain.Currency;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;


@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:test.properties")
public class CurrencyRepositoryTest {
    @Inject
    private CurrencyRepository currencyRepository;

    @Test
    public void testAddNewCurrency() throws Exception {
        Currency currency = new Currency("USD", "United States Dollar");
        currencyRepository.save(currency);
        Currency existing = currencyRepository.findOne("USD");
        Assert.assertNotNull(existing);
        Assert.assertEquals("USD", existing.getCode());
        Assert.assertEquals("United States Dollar", existing.getDescription());
        Assert.assertNull(currencyRepository.findOne("EUR"));
    }

    @Test
    public void testUpdateExistingCurrency() throws Exception {
        Currency currency = new Currency("EUR", "European Euro");
        currencyRepository.save(currency);
        Currency existing = currencyRepository.findOne("EUR");
        Assert.assertEquals("European Euro", existing.getDescription());
        existing.setDescription("European Union Euro");
        currencyRepository.save(existing);
        existing = currencyRepository.findOne("EUR");
        Assert.assertEquals("European Union Euro", existing.getDescription());
    }

    @Test
    public void testDeleteExistingCurrency() throws Exception {
        Currency currency = new Currency("EUR", "European Euro");
        currencyRepository.save(currency);
        Currency existing = currencyRepository.findOne("EUR");
        Assert.assertEquals("European Euro", existing.getDescription());
        currencyRepository.delete(existing);
        existing = currencyRepository.findOne("EUR");
        Assert.assertNull(existing);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void testFailedWithNull() throws Exception {
        currencyRepository.findOne((String) null);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testDeleteNoneExisting() throws Exception {
        currencyRepository.delete("EEE");
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testPRLength() throws Exception {
        currencyRepository.save(new Currency("EUR", "EUR"));
        currencyRepository.save(new Currency("EURO", "EUR"));
    }
}
