package org.openexchange.transactions;

import org.openexchange.domain.Currency;
import org.openexchange.domain.Rate;
import org.openexchange.repository.CurrencyRepository;
import org.openexchange.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Service
public class TransactionalService {
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private RateRepository rateRepository;

    @Transactional(rollbackFor = Exception.class)
    public void addAndRollbackAllOnException() throws Exception {
        atomicAction();
        throw new Exception("Something went wrong!");
    }

    @Transactional
    public void addAndCommitAll() throws Exception {
        atomicAction();
    }

    private void atomicAction(){
        Currency usd = new Currency("USD","United States Dollar");
        Currency eur = new Currency("EUR","European Euro");
        currencyRepository.save(usd);
        currencyRepository.save(eur);
        rateRepository.save(new Rate(eur, usd, BigDecimal.valueOf(0.86), Date.from(Instant.now())));
    }
}
