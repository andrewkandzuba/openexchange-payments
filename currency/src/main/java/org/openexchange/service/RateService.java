package org.openexchange.service;

import org.openexchange.domain.Currency;
import org.openexchange.domain.Rate;

import java.util.List;

public interface RateService {
    List<Rate> findRatesBySource(Currency source);
    Rate findRate(Currency source, Currency target);
}
