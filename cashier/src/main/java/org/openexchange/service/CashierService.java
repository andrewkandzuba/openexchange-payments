package org.openexchange.service;

import java.math.BigDecimal;

public interface CashierService {
    BigDecimal exchange(String source, String target, double amount);
}
