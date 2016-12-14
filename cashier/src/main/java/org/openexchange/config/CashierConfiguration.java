package org.openexchange.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
public class CashierConfiguration {
    @Value("${configuration.properties.language:EN}")
    private String language;

    @Value("${configuration.properties.currency:USD}")
    private String currency;

    public String getLanguage() {
        return language;
    }

    public String getCurrency() {
        return currency;
    }
}
