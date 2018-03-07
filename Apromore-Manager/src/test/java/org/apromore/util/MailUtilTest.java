/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.util;

import org.junit.After;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
