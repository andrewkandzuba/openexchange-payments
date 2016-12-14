package org.openexchange.integration;

import org.openexchange.pojos.Currencies;
import org.openexchange.pojos.Quotes;

import java.util.List;

public interface CurrencyLayerService {
    Currencies all() throws ServiceException;
    Quotes live(List<String> currencyCodes) throws ServiceException;
}
