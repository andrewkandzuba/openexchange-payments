package org.openexchange.jms;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class JmsUtilsTest {
    static final String TEST_JMS_QUEUE = "test.jms.queue";
    @Rule
    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();
    @Autowired
    protected JmsTemplate jmsTemplate;

    private void failure() throws JmsException {
        throw new UncategorizedJmsException("Something went wrong!!!");
    }

    @Transactional(rollbackFor = JmsException.class, propagation = Propagation.REQUIRES_NEW)
    void failedToSend(JmsTemplate jmsTemplate, String destination) {
        String s1 = "message1";
        String s2 = "message2";

        jmsTemplate.convertAndSend(destination, s1);
        failure();
        jmsTemplate.convertAndSend(destination, s2);
    }

    @Transactional(rollbackFor = JmsException.class, propagation = Propagation.REQUIRES_NEW)
    void successToSend(JmsTemplate jmsTemplate, String destination) {
        String s1 = "message1";
        String s2 = "message2";;

        jmsTemplate.convertAndSend(destination, s1);
        jmsTemplate.convertAndSend(destination, s2);
    }

    @Transactional(rollbackFor = JmsException.class, propagation = Propagation.REQUIRES_NEW)
    void failedRead(JmsTemplate jmsTemplate, String destination) {
        jmsTemplate.receiveAndConvert(destination);
        failure();
        jmsTemplate.receiveAndConvert(destination);
    }
}
