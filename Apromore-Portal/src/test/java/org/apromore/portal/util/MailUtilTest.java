package org.apromore.portal.util;

import org.junit.After;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

/**
 * Tests the Mail Utility.
 */
public class MailUtilTest {

    @After
    public void tearDown() throws Exception {
        Mailbox.clearAll();
    }

    @Test
    public void testSendingTextEmailTest() {
        List<Message> inbox;
        try {
            MailUtil.sendEmailText("someone@somewhere.com", "apromoreTester@somewhere.com", "Expected Subject", "Expected Body");
            inbox = Mailbox.get("someone@somewhere.com");

            assertThat(inbox.isEmpty(), is(false));

            Message message = inbox.get(0);
            assertThat("Expected Subject", equalTo(message.getSubject()));
            assertThat("Expected Body", equalTo(message.getContent()));
        } catch (MessagingException | IOException e) {
            fail("Unexpected exception trying to read Mailbox.");
        }
    }

    @Test
    public void testSendingHtmlEmailTest() {
        List<Message> inbox;
        try {
            MailUtil.sendEmailHtml("someone@somewhere.com", "apromoreTester@somewhere.com", "Expected Subject", "<html><h1>Heading</h1><p>The body paragraph</p></html>");
            inbox = Mailbox.get("someone@somewhere.com");

            assertThat(inbox.isEmpty(), is(false));

            Message message = inbox.get(0);
            assertThat("Expected Subject", equalTo(message.getSubject()));
            assertThat("text/html; charset=us-ascii", equalTo(message.getContentType()));
        } catch (MessagingException e) {
            fail("Unexpected exception trying to read Mailbox.");
        }
    }
}
