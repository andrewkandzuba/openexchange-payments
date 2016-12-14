package org.openexchange.controller;

import org.openexchange.domain.Currency;
import org.openexchange.domain.Rate;
import org.openexchange.service.CurrencyService;
import org.openexchange.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class CurrencyController {
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RateService rateService;

    @RequestMapping(path = "/currencies", method = RequestMethod.GET)
    public List<Currency> findAll() {
        return currencyService.findAll();
    }

    @RequestMapping(path = "/currencies/{code}", method = RequestMethod.GET)
    public Currency findByCode(@PathVariable String code) {
        Currency currency = currencyService.findByCode(code);
        Assert.notNull(currency, "The source currency does not exist");
        return currency;
    }

    @RequestMapping(path = "/quotes/{sourceCode}/{targetCode}", method = RequestMethod.GET)
    public BigDecimal findQuote(@PathVariable String sourceCode, @PathVariable String targetCode){
        Currency source = currencyService.findByCode(sourceCode);
        Assert.notNull(source, "The source currency does not exist");
        Currency target = currencyService.findByCode(targetCode);
        Assert.notNull(source, "The target currency does not exist");
        Rate rate = rateService.findRate(source, target);
        Assert.notNull(rate, "A rate does not exist for the combination of the source and the target currencies");
        return rate.getQuote();
    }
}
