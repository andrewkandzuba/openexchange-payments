package org.openexchange.jobs;

import org.openexchange.domain.Currency;
import org.openexchange.domain.Rate;
import org.openexchange.pojos.Quote;
import org.openexchange.quote.QuoteService;
import org.openexchange.repository.CurrencyRepository;
import org.openexchange.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

@Configuration
@RefreshScope
public class JobsConfiguration {
    @Value("${sms.outbound.queue.read.chunk.size:100}")
    private int chunckSize;
    private final QuoteService quoteService;
    private final RateRepository rateRepository;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public JobsConfiguration(QuoteService quoteService, RateRepository rateRepository, CurrencyRepository currencyRepository) {
        this.quoteService = quoteService;
        this.rateRepository = rateRepository;
        this.currencyRepository = currencyRepository;
    }

    @Job(parallelism = "${spring.consumers.concurrency:4}")
    public void jobQuotesConsume() {
        while (!Thread.currentThread().isInterrupted()) {
            consume();
        }
    }

    @Transactional(rollbackFor = Throwable.class, timeout = 5)
    private void consume() {
        while (true) {
            Collection<Quote> messages = quoteService.read(chunckSize);
            if (messages.isEmpty()) {
                return;
            }
            Collection<Rate> records = new HashSet<>();
            messages.forEach(quote -> {
                Currency source = currencyRepository.findOne(quote.getSource());
                Currency target = currencyRepository.findOne(quote.getTarget());
                if (source != null && target != null) {
                    Rate rate = new Rate(source, target, BigDecimal.valueOf(quote.getQuote()), quote.getTimestamp());
                    records.add(rate);
                }

            });
            rateRepository.save(records);
        }
    }
}
