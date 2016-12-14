package org.openexchange.jobs;

import org.openexchange.integration.CurrencyLayerService;
import org.openexchange.integration.ServiceException;
import org.openexchange.pojos.Quote;
import org.openexchange.pojos.Quotes;
import org.openexchange.quote.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
@RefreshScope
public class CurrencylayerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(CurrencylayerConfiguration.class);

    private final CurrencyLayerService currencyLayerService;
    private final QuoteService quoteService;

    public CurrencylayerConfiguration(CurrencyLayerService currencyLayerService, QuoteService quoteService) {
        this.currencyLayerService = currencyLayerService;
        this.quoteService = quoteService;
    }

    @Job(parallelism = "1")
    public void update(){
        try {
            List<String> currencyCodes = Arrays.asList(currencyLayerService.all().getCurrencies().keySet().stream().toArray(String[]::new));
            Quotes response = currencyLayerService.live(currencyCodes);
            Date timestamp = Date.from(Instant.now());
            List<Quote> quotes = new CopyOnWriteArrayList<>();
            response.getQuotes()
                    .entrySet()
                    .forEach(entry -> quotes.add(build(entry.getKey().substring(0, 3), entry.getKey().substring(3, 6), entry.getValue(), timestamp)));
            quoteService.write(quotes);
        } catch (ServiceException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static Quote build(String source, String target, Double rate, Date timestamp) {
        Quote quote = new Quote();
        quote.setSource(source);
        quote.setTarget(target);
        quote.setQuote(rate);
        quote.setTimestamp(timestamp);
        return quote;
    }
}