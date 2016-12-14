package org.openexchange.ampq;

import org.openexchange.pojos.Quote;
import org.openexchange.quote.QuoteService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;

@Configuration
@RefreshScope
@EnableTransactionManagement
public class AmpqAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public PlatformTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        template.setRetryTemplate(retryTemplate);
        return template;
    }

    @ConditionalOnMissingBean(QuoteService.class)
    @Bean
    public QuoteService quoteService(AmqpTemplate rabbitTemplate) {
        return new QuoteService() {
            private final String QUOTES_QUEUE = "quotes.queue";

            @Override
            @Transactional(rollbackFor = Throwable.class)
            public void write(Collection<Quote> list) {
                list.forEach(o -> rabbitTemplate.convertAndSend(QUOTES_QUEUE, o));
            }

            @Override
            @Transactional(rollbackFor = Throwable.class)
            public Collection<Quote> read(int number) {
                Collection<Quote> messages = new HashSet<>();
                while (number-- > 0) {
                    Quote quote = (Quote) rabbitTemplate.receiveAndConvert(QUOTES_QUEUE);
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
