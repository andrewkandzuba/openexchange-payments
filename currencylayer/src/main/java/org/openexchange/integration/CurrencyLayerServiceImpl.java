package org.openexchange.integration;

import org.openexchange.pojos.Currencies;
import org.openexchange.pojos.Quotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@EnableRetry
public class CurrencyLayerServiceImpl implements CurrencyLayerService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyLayerServiceImpl.class.getName());
    @Autowired
    private RestTemplate restTemplate;
    @Value("${currencylayer.api.accesskey}")
    private String accessKey;
    @Value("${currencylayer.api.endpoint}")
    private String endpoint;

    @Override
    @Retryable(value = {ServiceException.class}, maxAttempts = 5, backoff = @Backoff(random = true, multiplier = 2))
    public Currencies all() throws ServiceException {
        logger.info("Retrieves value from an external service");
        ResponseEntity<Currencies> resp = restTemplate.getForEntity(
                endpoint + "/api/list?access_key={access_key}&format={format}",
                Currencies.class,
                accessKey, 1);
        logger.info("The response status is " + resp.getStatusCode());
        if (!resp.getStatusCode().equals(HttpStatus.OK)) {
            throw new ServiceException("Unable to retrieve the list of currencies! Response details: " + resp.toString());
        }
        return resp.getBody();
    }

    @Override
    @Retryable(value = {ServiceException.class}, maxAttempts = 5, backoff = @Backoff(random = true, multiplier = 2))
    public Quotes live(List<String> currencyCodes) throws ServiceException {
        logger.info("Retrieves value from an external service");
        ResponseEntity<Quotes> resp = restTemplate.getForEntity(
                endpoint + "/api/live?access_key={access_key}&currencies={currenciesCodes}&format={format}",
                Quotes.class,
                accessKey,
                StringUtils.arrayToCommaDelimitedString(currencyCodes.toArray()), 1);
        logger.info("The response status is " + resp.getStatusCode());
        if (!resp.getStatusCode().equals(HttpStatus.OK)) {
            throw new ServiceException("Unable to retrieve the list of quotes! Response details: " + resp.toString());
        }
        return resp.getBody();
    }

    @Recover
    public void recover(ServiceException e) {
        logger.error("Recover from: " + e.getMessage());
    }
}
