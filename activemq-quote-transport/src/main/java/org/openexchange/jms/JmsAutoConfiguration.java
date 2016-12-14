package org.openexchange.jms;

import org.openexchange.pojos.Quote;
import org.openexchange.quote.QuoteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.ConnectionFactory;
import javax.jms.Session;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Configuration
@RefreshScope
@EnableJms
@EnableTransactionManagement
public class JmsAutoConfiguration {
    @Value("${spring.jms.broker.receive.timeout.timeUnit:SECONDS}")
    private String receiveTimeoutTimeUnit;
    @Value("${spring.jms.broker.receive.timeout.interval:1}")
    private long receiveTimeoutInterval;

    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public PlatformTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new JmsTransactionManager(connectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        jmsTemplate.setSessionTransacted(true);
        jmsTemplate.setReceiveTimeout(TimeUnit.valueOf(receiveTimeoutTimeUnit).toMillis(receiveTimeoutInterval));
        return jmsTemplate;
    }

    @ConditionalOnMissingBean(QuoteService.class)
    @Bean
    public QuoteService quoteService(JmsTemplate jmsTemplate) {
        return new QuoteService() {
            private final String QUOTES_QUEUE = "quotes.queue";

            @Override
            @Transactional(rollbackFor = Throwable.class)
            public void write(Collection<Quote> list) {
                list.forEach(o -> jmsTemplate.convertAndSend(QUOTES_QUEUE, o));
            }

            @Override
            @Transactional(rollbackFor = Throwable.class)
            public Collection<Quote> read(int number) {
                Collection<Quote> messages = new HashSet<>();
                while (number-- > 0) {
                    Quote quote = (Quote) jmsTemplate.receiveAndConvert(QUOTES_QUEUE);
                    if (quote == null) {
                        break;
                    }
                    messages.add(quote);
                }
                return messages;
            }
        };
    }
}
