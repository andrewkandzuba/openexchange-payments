package org.openexchange.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;

@FeignClient(name = "currency")
public interface CurrencyClient {
    @RequestMapping(path = "quotes/{sourceCode}/{targetCode}", method = RequestMethod.GET)
    BigDecimal findQuote(@PathVariable("sourceCode") String sourceCode, @PathVariable("targetCode") String targetCode);
}
