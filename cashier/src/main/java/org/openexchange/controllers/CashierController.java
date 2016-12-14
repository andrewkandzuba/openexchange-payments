package org.openexchange.controllers;

import org.openexchange.config.CashierConfiguration;
import org.openexchange.service.CashierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class CashierController {
    @Autowired
    private Environment environment;
    @Autowired
    private CashierConfiguration cashierConfiguration;
    @Autowired
    private CashierService cashierService;

    @RequestMapping("/")
    public String query(@RequestParam("q") String q) {
        return environment.getProperty(q);
    }

    @RequestMapping("/language")
    public String language() {
        return cashierConfiguration.getLanguage();
    }

    @RequestMapping("/currency")
    public String currency() {
        return cashierConfiguration.getCurrency();
    }

    @RequestMapping("/exchange/{source}/{target}/{amount}")
    public BigDecimal exchange(@PathVariable String source, @PathVariable String target, @PathVariable double amount){
        return cashierService.exchange(source, target, amount);
    }
}
