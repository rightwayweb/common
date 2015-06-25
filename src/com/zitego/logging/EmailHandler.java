package com.zitego.logging;

import java.util.logging.*;
import com.zitego.util.TextUtils;
import com.zitego.mail.SMTPMail;

/**
 * A logging handler for use with java logging facility. Sends email when a loggable
 * message is published.
 *
 * @author John Glorioso
 * @version $Id: EmailHandler.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class EmailHandler extends Handler
{
    /** The email addresses in a space, comma, or semi-colon delimited string. */
    protected String _addresses;
    /** The mail server to use when sending. */
    protected String _mailServer;
    /** The from address to use. */
    protected String _fromAddress;
    /** The mail subject. */
    protected String _subject;
    /** The username to use for smtp authentication. */
    protected String _username;
    /** The password to use for smtp authentication. */
    protected String _password;

    /**
     * Creates a handler allowing user-defined severity level and From: address.
     *
     * @param String The delimited list of message recipient addresses.
     * @param String The mail server to use for message delivery.
     * @param String The the From: address on mail sent.
     * @param String The email subject line.
     * @param String The severity level (and higher) which should receive messages.
     */
    protected EmailHandler(String addresses, String mailServer, String fromAddress, String subject, Level severity)
    {
        this (addresses, mailServer, fromAddress, subject, severity, null, null);
    }

    /**
     * Creates a handler allowing user-defined severity level and From: address.
     *
     * @param String The delimited list of message recipient addresses.
     * @param String The mail server to use for message delivery.
     * @param String The the From: address on mail sent.
     * @param String The email subject line.
     * @param String The severity level (and higher) which should receive messages.
     * @param String The username to use for smtp authentication.
     * @param String The password to use for smtp authentication.
     */
    protected EmailHandler(String addresses, String mailServer, String fromAddress, String subject, Level severity,
                           String username, String password)
    {
        _addresses = TextUtils.removeExtraSpaces( addresses.trim() );
        _mailServer = mailServer;
        setLevel(severity);
        if (fromAddress != null) fromAddress = TextUtils.removeExtraSpaces( fromAddress.trim() );
        _fromAddress = fromAddress;
        _subject = subject;
        _username = username;
        _password = password;
    }

    /**
     * Sets the addresses.
     *
     * @param String The delimited string of addresses.
     */
    public void setAddresses(String addresses)
    {
        _addresses = addresses;
    }

    /**
     * Returns the delimited string of email addresses.
     *
     * @return String
     */
    public String getAddresses()
    {
        return _addresses;
    }

    /**
     * Sets the mail server.
     *
     * @param String The server.
     */
    public void setMailServer(String mailServer)
    {
        _mailServer = mailServer;
    }

    /**
     * Returns the mail server.
     *
     * @return String
     */
    public String getMailServer()
    {
        return _mailServer;
    }

    /**
     * Sets the from address. This can be null. If it is, then the machine account name is used.
     *
     * @param String the from address.
     */
    public void setFromAddress(String fromAddress)
    {
        _fromAddress = fromAddress;
    }

    /**
     * Returns the from address.
     *
     * @return String
     */
    public String getFromAddress()
    {
        return _fromAddress;
    }

    /**
     * Sets the subject of the logger.
     *
     * @param String The subject.
     */
    public void setSubject(String subject)
    {
        _subject = subject;
    }

    /**
     * Returns the subject.
     *
     * @return String
     */
    public String getSubject()
    {
        return _subject;
    }

    /**
     * Sets the username to use for smtp authentication.
     *
     * @param String The username.
     */
    public void setUsername(String username)
    {
        _username = username;
    }

    /**
     * Returns the username to use for smtp authentication.
     *
     * @return String
     */
    public String getUsername()
    {
        return _username;
    }

    /**
     * Sets the password to use for smtp authentication.
     *
     * @param String The password.
     */
    public void setPassword(String password)
    {
        _password = password;
    }

    /**
     * Returns the password to use for smtp authentication.
     *
     * @return String
     */
    public String getPassword()
    {
        return _password;
    }

    public void close() { }

    public void flush() { }

    /**
     * Sends an email if a loggable record is published with a severe enough level.
     *
     * @param LogRecord The log record to publish.
     */
    public void publish(LogRecord record)
    {
        if ( isLoggable(record) )
        {
            try
            {
                SMTPMail mail = new SMTPMail(_mailServer);
                if (_fromAddress != null) mail.setFromAddress(_fromAddress);
                mail.setSubject(_subject);
                mail.setBody( record.getMessage() );
                mail.setToAddresses(_addresses);
                if (_username != null && _password != null) mail.setPasswordAuthentication(_username, _password);
                mail.sendMail();
            }
            catch (Exception ex)
            {
                getErrorManager().error
                (
                    "Can't send email to " + _addresses + " via " + _mailServer, ex, ErrorManager.GENERIC_FAILURE
                );
            }
        }
    }
}