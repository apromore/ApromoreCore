package org.apromore.portal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Utility to Help Sending Emails.
 *
 * @author Cameron James
 * @since 1.0
 */
public class MailUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);

    /**
     * Method that will send and email to an address from an address with custom subject and body.
     * @param to to recipients emails address.
     * @param from from the senders email address (most likely apromore@qut.edu.au)
     * @param subject the subject of the email
     * @param bodyMessage the body message for the email.
     */
    public static void sendEmailText(String to, String from, String subject, String bodyMessage) {
        Properties props = getEmailServerProperties();
        Session session = getEmailServerSession(props);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(bodyMessage);

            Transport.send(message);

            LOGGER.debug("Sent Email to " + to + " Done!");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Method that will send and email to an address from an address with custom subject and HTML as the body.
     * @param to to recipients emails address.
     * @param from from the senders email address (most likely apromore@qut.edu.au)
     * @param subject the subject of the email
     * @param bodyMessage the body message for the email with HTML tag inside.
     */
    public static void sendEmailHtml(String to, String from, String subject, String bodyMessage) {
        Properties props = getEmailServerProperties();
        Session session = getEmailServerSession(props);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(bodyMessage, "text/html");

            Transport.send(message);

            LOGGER.debug("Sent Email to " + to + " Done!");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    /* Creates the properties used to connect to the email server. */
    private static Properties getEmailServerProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail-relay.qut.edu.au");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "true");
        return props;
    }

    /* Creates a session with the email server. */
    private static Session getEmailServerSession(Properties props) {
        return Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("username", "password");
                    }
                });
    }
}
