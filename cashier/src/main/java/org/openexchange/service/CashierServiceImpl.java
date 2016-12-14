package org.openexchange.service;

import org.openexchange.client.CurrencyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CashierServiceImpl implements CashierService {
    @Autowired
    private CurrencyClient currencyClient;

    @Override
    public BigDecimal exchange(String source, String target, double amount) {
        BigDecimal quote = currencyClient.findQuote(source, target);
        return BigDecimal.valueOf(amount).multiply(quote);
    }
}
