package org.openexchange.jms;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.JmsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JmsTransactionalSendTest.class)
@SpringBootApplication
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:test.properties")
public class JmsTransactionalReceiveTest extends JmsUtilsTest {
    @Test
    @Transactional
    public void readRollback() {
        Assert.assertNull(jmsTemplate.receiveAndConvert(TEST_JMS_QUEUE));
        successToSend(jmsTemplate, TEST_JMS_QUEUE);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        TestTransaction.start();
        try {
            failedRead(jmsTemplate, TEST_JMS_QUEUE);
            TestTransaction.flagForCommit();
        } catch (JmsException ignored){
            TestTransaction.flagForRollback();
        }
        TestTransaction.end();

        Assert.assertNotNull(jmsTemplate.receive(TEST_JMS_QUEUE));
        Assert.assertNotNull(jmsTemplate.receive(TEST_JMS_QUEUE));
        Assert.assertNull(jmsTemplate.receive(TEST_JMS_QUEUE));
    }
}
