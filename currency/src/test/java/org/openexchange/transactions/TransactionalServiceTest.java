package org.openexchange.transactions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openexchange.repository.CurrencyRepository;
import org.openexchange.repository.RateRepository;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

//org.openexchange.repository
@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan(basePackages = {
        "org.openexchange.repository",
        "org.openexchange.transactions"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@TestPropertySource(locations = "classpath:test.properties")
public class TransactionalServiceTest {
    @Inject
    private CurrencyRepository currencyRepository;
    @Inject
    private RateRepository rateRepository;
    @Inject
    private TransactionalService transactionalService;

    @Test
    public void testRollbackAll() throws Exception {
        try {
            transactionalService.addAndRollbackAllOnException();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Assert.assertEquals(0, currencyRepository.findAll().size());
            Assert.assertEquals(0, rateRepository.findAll().size());
        }
    }

    @Test
    public void testCommitAll() throws Exception {
        transactionalService.addAndCommitAll();
        Assert.assertEquals(2, currencyRepository.findAll().size());
        Assert.assertEquals(1, rateRepository.findAll().size());
    }
}
