package org.openexchange.service;

import org.openexchange.domain.Currency;

import java.util.List;

public interface CurrencyService {
    List<Currency> findAll();
    Currency findByCode(String code);
    Currency create(String code, String description);
    void saveChanges(Currency currency);
    void delete(String code);
}
