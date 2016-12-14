package org.openexchange.jms;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openexchange.pojos.Quote;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.JmsException;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JmsTransactionalSendTest.class)
@SpringBootApplication
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:test.properties")
public class JmsTransactionalSendTest extends JmsUtilsTest {
    @Test
    @Transactional
    public void transactionRollback() throws InterruptedException {
        Assert.assertNull(jmsTemplate.receive(TEST_JMS_QUEUE));
        try {
            failedToSend(jmsTemplate, TEST_JMS_QUEUE);
        } catch (JmsException ignored){
            TestTransaction.end();
        }
        Assert.assertNull(jmsTemplate.receive(TEST_JMS_QUEUE));
    }

    @Test
    @Transactional
    @Commit
    public void transactionCommit() throws InterruptedException {
        Assert.assertNull(jmsTemplate.receive(TEST_JMS_QUEUE));
        successToSend(jmsTemplate, TEST_JMS_QUEUE);
        TestTransaction.end();
        Assert.assertNotNull(jmsTemplate.receive(TEST_JMS_QUEUE));
        Assert.assertNotNull(jmsTemplate.receive(TEST_JMS_QUEUE));
        Assert.assertNull(jmsTemplate.receive(TEST_JMS_QUEUE));
    }


    @Test
    public void dateConversionTests() throws Exception {
        Date d = DateUtils.parseDate("2011-12-03 10:15:30", "yyyy-MM-dd hh:mm:ss");

        Quote s = new Quote();
        s.setSource("USD");
        s.setTarget("EUR");
        s.setQuote(0.9d);
        s.setTimestamp(d);

        jmsTemplate.convertAndSend(TEST_JMS_QUEUE, s);

        Quote r = (Quote) jmsTemplate.receiveAndConvert(TEST_JMS_QUEUE);
        Assert.assertEquals(r.getSource(), "USD");
        Assert.assertEquals(r.getTarget(), "EUR");
        Assert.assertTrue(BigDecimal.valueOf(s.getQuote()).compareTo(BigDecimal.valueOf(0.9d)) == 0);
        Assert.assertEquals(r.getTimestamp(), d);
    }
}
