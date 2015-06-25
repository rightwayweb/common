package com.zitego.mail;

import com.zitego.util.TextUtils;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Date;
import java.util.Enumeration;
import javax.mail.Authenticator;
import javax.mail.Address;
import javax.mail.PasswordAuthentication;
import javax.mail.MessagingException;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import com.sun.mail.smtp.SMTPMessage;

/**
 * This is a class intended to facilitate the sending of e-mail via an SMTP server.
 *
 * Emails can be easily sent using the example below.<p>
 *
 * SMTPMail mail = new SMTPMail();<br>
 * mail.setMailServer("127.0.0.1");<br>
 * mail.setFromAddress("from@email.com");<br>
 * mail.setToAddress("to@email.com");<br>
 * mail.setSubject("Subject of email");<br>
 * mail.setBody("This is the body of the message);<br>
 * mail.sendMail();<br>
 *
 * @author John Glorioso
 * @version $Id: SMTPMail.java,v 1.3 2012/12/30 07:00:33 jglorioso Exp $
 */
public class SMTPMail extends Authenticator
{
    /** The default number of times to retry. */
    protected static final int DEFAULT_RETRIES = 10;
    /** The default delay in milliseconds between retries. */
    protected static final long DEFAULT_RETRY_DELAY = 1000l;
    /** The smtp server. */
    protected String _smtpServer = "";
    /** The smtp port (25 by default). */
    protected int _smtpPort = 25;
    /** The from address. */
    protected InternetAddress _sender;
    /** The envelope from address. */
    protected String _envelopeFrom;
    /** The reply to address. */
    protected InternetAddress _replyTo;
    /** The recipients. */
    protected Vector _recipients;
    /** The cc recipients. */
    protected Vector _ccRecipients;
    /** The bcc recipients. */
    protected Vector _bccRecipients;
    /** The parts of the message. */
    protected Vector _multipartMessages;
    /** The subject. */
    protected String _subject = "";
    /** The plain text body. */
    protected String _body = "";
    /** The number of times to retry. */
    protected int _numberOfRetries = DEFAULT_RETRIES;
    /** The delay in milliseconds between retries. */
    protected long _retryDelay = DEFAULT_RETRY_DELAY;
    /** The attachments. */
    protected Hashtable _attachments;
    /** The mime type. */
    protected String _contentType;
    /**  The headers to set. */
    protected Hashtable _headers;
    /** The authentication to use for smtp auth. */
    protected PasswordAuthentication _authentication;

    public static void main(String args[])
    {
        try
        {
            SMTPMail mail = new SMTPMail("localhost");
            mail.setToAddress(args[0]);
            mail.setFromAddress(args[1]);
            mail.setSubject(args[2]);
            mail.setBody(args[3]);
            mail.setReplyToAddress(args[4]);
            mail.sendMail();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }

    /**
     * Creates a new SMTPMail object.
     */
    public SMTPMail()
    {
        super();
    }

    /**
     * Creates a new SMTPMail object with a mail host.
     *
     * @param host The host.
     */
    public SMTPMail(String host)
    {
        super();
        _smtpServer = host;
    }

    /**
     * Sets the password authentication for this smtp mail. If either value is null,
     * the authentication is reset to nothing.
     *
     * @param user The user to authenticate as.
     * @param pass The password to use.
     */
    public void setPasswordAuthentication(String user, String pass)
    {
        if (user != null && pass != null) _authentication = new PasswordAuthentication(user, pass);
        else _authentication = null;
    }

    /**
     * Authenticator method. Returns the Password authentication for this user/pass.
     *
     * @return PasswordAuthentication
     */
    protected PasswordAuthentication getPasswordAuthentication()
    {
        return _authentication;
    }

    /**
     * sets the server to use to send the email.
     *
     * @param server the SMTP server name or ip.
     */
    public void setMailServer(String server)
    {
        _smtpServer = server;
    }

    /**
     * Sets the smtp port to use to send the mail.
     *
     * @param port The port.
     */
    public void setPort(int port)
    {
        _smtpPort = port;
    }

    /**
     * Sets the email address that will be used for the sender of the email. This address will
     * appear in the from line of the email.
     *
     * @param senderEmail The email address of the sender of this email.
     * @throws AddressException if the email address is not formatted properly.
     */
    public void setFromAddress(String senderEmail) throws AddressException
    {
        _sender = new InternetAddress(senderEmail);
    }

    /**
     * Sets the email address that will be used for the sender of the email. This address will
     * appear in the from line of the email.
     *
     * @param senderEmail The email address of the sender of this email.
     * @param senderPersonalName The name of the sender.
     * @throws UnsupportedEncodingException if there is a problem with the email address for the underlying mailer
     *                                      does not support personal names.
     */
    public void setFromAddress(String senderEmail, String senderPersonalName) throws UnsupportedEncodingException
    {
        _sender = new InternetAddress(senderEmail, senderPersonalName);
    }

    /**
     * Sets the email address that the envelope will be reported from. This is different then the from address
     * in that it is the address that bounces will go to.
     *
     * @param senderEmail The email address of the sender of this email.
     */
    public void setEnvelopeFrom(String senderEmail)
    {
        _envelopeFrom = senderEmail;
    }

    /**
     * Returns the email address that will be used for the sender of the email as an InternetAddress.
     *
     * @return InternetAddress
     */
    public InternetAddress getFromAddress()
    {
        return _sender;
    }

    /**
     * Returns the email address that the envelope will be reported from.
     *
     * @return String
     */
    public String getEnvelopeFrom()
    {
        return _envelopeFrom;
    }

    /**
     * Sets the email address that will be used for the reply-to address of the email.
     *
     * @param replyTo The reply to address.
     * @throws AddressException if the email address is not formatted properly.
     */
    public void setReplyToAddress(String replyTo) throws AddressException
    {
        _replyTo = new InternetAddress(replyTo);
    }

    /**
     * Sets the email address that will be used for the reply-to address of the email.
     *
     * @param replyTo The reply to address.
     * @param name The name of the sender.
     * @throws UnsupportedEncodingException if there is a problem with the email address for the underlying mailer
     *                                      does not support personal names.
     */
    public void setReplyToAddress(String replyTo, String name) throws UnsupportedEncodingException
    {
        _replyTo = new InternetAddress(replyTo, name);
    }

    /**
     * Returns the reply-to email address.
     *
     * @return InternetAddress
     */
    public InternetAddress getReplyToAddress()
    {
        return _replyTo;
    }

    /**
     * Sets the recipients email address. This method can be called more than once to add multiple
     * recipients.
     *
     * @param toEmail the recipients email address.
     * @throws AddressException if the email address is not formatted properly.
     */
    public void setToAddress(String toEmail) throws AddressException
    {

        InternetAddress recp = new InternetAddress(toEmail);
        if (_recipients == null) _recipients = new Vector();
        _recipients.addElement(recp);
    }

    /**
     * Sets multiple recipients email addresses. This method can be called
     * more than once to add multiple recipients. Recipients can be
     * delimited by white space, semi-colons, or commas.
     *
     * @param toAddresses The recipients email address string.
     * @throws AddressException if the email addresses are not formatted properly.
     */
    public void setToAddresses(String toAddresses) throws AddressException
    {
        toAddresses = toAddresses.replace(';', ' ').replace(',', ' ');
        String[] recipients = TextUtils.split(toAddresses.trim(), ' ');
        for (int i=0; i<recipients.length; i++)
        {
             setToAddress(recipients[i]);
        }
    }


    /**
     * Sets the carbon copy recipients email address. This method can be called more than once to add multiple
     * carbon copy recipients.
     *
     * @param toEmail The carbon copy recipients email address.
     * @throws AddressException if the email address is not formatted properly.
     */
    public void setCCAddress(String toEmail) throws AddressException{

        InternetAddress recp = new InternetAddress(toEmail);

        if( _ccRecipients == null) _ccRecipients = new Vector();

        _ccRecipients.addElement(recp);
    }

    /**
     * sets the blind carbon copy recipients email address.  This method can be called more than once to add multiple
     * blind carbon copy recipients.  Blind carbon copy recipients remain unknown to other recipients of the email
     *
     * @param toEmail the blind carbon copy recipients email address
     * @throws AddressException - if the email address is not formatted properly
     */
    public void setBCCAddress(String toEmail) throws AddressException
    {
        InternetAddress recp = new InternetAddress(toEmail);
        if (_bccRecipients == null) _bccRecipients = new Vector();
        _bccRecipients.addElement(recp);
    }

    /**
     * sets the subject of the email
     *
     * @param subject The subject.
     */
    public void setSubject(String subject)
    {
        _subject = subject;
    }

    /**
     * Returns the message subject.
     *
     * @return String
     */
    public String getSubject()
    {
        return _subject;
    }

    /**
     * Sets the message body.
     *
     * @param body The message text.
     */
    public void setBody(String body)
    {
        _body = body;
    }

    /**
     * returns the message body.
     *
     * @return String
     */
    public String getBody()
    {
        return _body;
    }

    /**
     * Sets the number of attempts to make when sending the message. This is to account for
     * network hiccups that would otherwise result in a failed send. If an argument less than
     * 1 is given, then 1 is used.
     *
     * @param RetryCount The number of attempts to make.
     */
    public void setRetryCount(int RetryCount)
    {
        _numberOfRetries = (RetryCount < 1 ? 1 : RetryCount);
    }

    /**
     * Returns the number of retry attempts used when sending the message.
     *
     * @return int
     */
    public int getRetryCount()
    {
        return _numberOfRetries;
    }

    /**
     * Sets the delay between retry attempts when sending the message. If a delay of less than
     * 0 is given, then 0 is used.
     *
     * @param retryDelay The delay.
     */
    public void setRetryDelay(long retryDelay)
    {
        _retryDelay = (retryDelay < 0 ? 0 : retryDelay);
    }

    /**
     * Returns the current delay between retry attempts when sending the message.
     *
     * @return long
     */
    public long getRetryDelay()
    {
        return _retryDelay;
    }

    /**
     * Add a file attachment to this email. The file must exist as a file on the local filesystem.
     * The filename that will be used when sending the email will be the same as the filelocation.
     * To provide a filename, use the addAttachment(String, String) method
     *
     * @param fileLocation The location of the file to attach.
     * @throws FileNotFoundException if the file does not exist.
     */
    public void addAttachment(String filelocation) throws FileNotFoundException
    {
        addAttachment(filelocation, filelocation);
    }

    /**
     * Add a file attachment to this email. The file must exist as a file on the local filesystem.
     *
     * @param fileLocation The location of the file to attach.
     * @param name The name to give the file being attached within the email.
     * @throws FileNotFoundException
     */
    public void addAttachment(String filelocation, String name) throws FileNotFoundException
    {
        File file = new File(filelocation);
        if ( !file.exists() ) throw new FileNotFoundException("File not found at: " + filelocation);

        if (_attachments == null) _attachments = new Hashtable();

        if (name == null) name = filelocation;

        _attachments.put(name, filelocation);
    }

    /**
     * Add a header to this email.
     *
     * @param name The header name.
     * @param value The header value.
     * @throws IllegalArgumentException if either the name or value are null.
     */
    public void addHeader(String name, String value) throws IllegalArgumentException
    {
        if (name == null || value == null) throw new IllegalArgumentException("Both name and value must be specified");
        if (_headers == null) _headers = new Hashtable();
        _headers.put(name, value);
    }

    /**
     * Adds a new multipart message body part. The text and mimetype are needed. The content
     * supplied to setContent will be ignored if multipart messages are added.
     *
     * @param msg The message text.
     * @param type The content type.
     * @throws MessagingException if the content type is not valid.
     */
    public void addMultipartMessage(String msg, String type) throws MessagingException
    {
        if (_multipartMessages == null) _multipartMessages = new Vector();
        MimeBodyPart p = new MimeBodyPart();
        p.setContent(msg, type);
        _multipartMessages.add(p);
    }

    /**
     * Sets the content type of the email message.
     *
     * @param type The string representation of the type.
     */
    public void setContentType(String type)
    {
        _contentType = type;
    }

    /**
     * Returns the content type of the email message.
     *
     * @return String
     */
    public String getContentType()
    {
        return  _contentType;
    }


    /**
     * Sends the message to the supplied recipients with the default retry settings.
     *
     * @param message The message.
     * @param address The addresses.
     */
    public static void send(MimeMessage message, String[] address) throws MessagingException
    {
        send(message, DEFAULT_RETRIES, DEFAULT_RETRY_DELAY, address);
    }

    /**
     * Sends the message to the supplied recipients with the supplied retry settings.
     *
     * @param message The message.
     * @param retry The number of times to retry.
     * @param retryDelay The delay between each retry.
     * @param address The addresses.
     */
    public static void send(MimeMessage message, int retry, long retryDelay, String[] address) throws MessagingException
    {
        if (address == null || address.length < 1) return;

        InternetAddress recps[] = new InternetAddress[address.length];
        if (address != null && address.length >= 1)
        {
            for (int i = 0; i < address.length; i++)
            {
                recps[i] = new InternetAddress(address[i]);
            }
        }
        send(message, retry, retryDelay, recps);
    }

    /**
     * Sends the message to the supplied recipients with the default retry settings.
     *
     * @param message the message to send.
     * @param recipients The list of addresses to send to.
     */
    public static void send(MimeMessage message, InternetAddress[] recipients) throws MessagingException
    {
        send(message, DEFAULT_RETRIES, DEFAULT_RETRY_DELAY, recipients);
    }

    /**
     * Sends the message to the supplied recipients with the supplied retry settings.
     *
     * @param message The message to send.
     * @param retry The number of times to retry.
     * @param retryDelay The delay between each retry.
     * @param recipients  The list of addresses to send to.
     * @throws MessagingException when an error occurs sending.
     */
    public static void send(MimeMessage message, int retry, long retryDelay, InternetAddress[] recipients)
    throws MessagingException
    {
        if (message == null || recipients == null || recipients.length < 1) return;

        // Save any current recipients
        Address[] originalToRecipients = message.getRecipients(Message.RecipientType.TO);
        Address[] originalCCRecipients = message.getRecipients(Message.RecipientType.CC);
        Address[] originalBCCRecipients = message.getRecipients(Message.RecipientType.BCC);
        try
        {
            boolean bSent = false;
            int sendTries = 0;
            message.setRecipients(Message.RecipientType.CC, new InternetAddress[0]);
            message.setRecipients(Message.RecipientType.BCC, new InternetAddress[0]);
            for (int i=0; i<recipients.length; i++)
            {
                bSent = false;
                sendTries = 0;

                message.setRecipients(Message.RecipientType.TO, new InternetAddress[] {recipients[i]});
                while (!bSent && sendTries < retry)
                {
                    try
                    {
                        sendTries++;
                        Transport.send(message);
                        bSent = true;
                        //pause for 1 second before retry, if this wasn't the last try
                        if (sendTries < retry)
                        {
                            try
                            {
                                Thread.currentThread().sleep(retryDelay);
                            }
                            catch(java.lang.InterruptedException ie) { }
                        }
                    }
                    catch(javax.mail.SendFailedException se)
                    {
                        //if we have reached the maximum number of tries, then throw the exception
                        if (sendTries == retry) throw se;
                    }
                }
            }
        }
        finally
        {
            // Restore original recipients
            if (message != null)
            {
                message.setRecipients(Message.RecipientType.TO, originalToRecipients);
                message.setRecipients(Message.RecipientType.CC, originalCCRecipients);
                message.setRecipients(Message.RecipientType.BCC, originalBCCRecipients);
            }
        }
    }

    /**
     * Sends the email message.
     *
     * @throws MessagingException if an error occurs while attempting to send the email.
     *                            Such errors include, but are not limited to, no server was set,
     *                            no recipient was defined, etc...
     */
    public void sendMail() throws MessagingException
    {
        Properties props = System.getProperties();

        //Error if the server is null or blank
        if ( _smtpServer == null || "".equals(_smtpServer) ) throw new MessagingException("No Mail Server defined");

        //Store the server
        props.put("mail.smtp.host", _smtpServer);

        //Set the port if not 25
        if (_smtpPort != 25) props.put("mail.smtp.port", _smtpPort);

        //If authentication was set, set it in the props
        Session s = null;
        if (_authentication != null)
        {
            props.put( "mail.user", _authentication.getUserName() );
            props.put("mail.smtp.auth", "true");
            s = Session.getDefaultInstance(props, this);
        }
        else
        {
            s = Session.getDefaultInstance(props, null);
        }

        // Construct the message object with a default session
        MimeMessage msg = new SMTPMessage(s);
        msg.setFrom(_sender);
        if (_envelopeFrom != null) ( (SMTPMessage)msg ).setEnvelopeFrom(_envelopeFrom);
        if (_replyTo != null) msg.setReplyTo( new InternetAddress[] { _replyTo } );
        else msg.setReplyTo( new InternetAddress[] { _sender } );

        InternetAddress recps[] = null;
        //To recipients
        if (_recipients != null)
        {
            recps = new InternetAddress[_recipients.size()];
            _recipients.copyInto(recps);
            msg.setRecipients(Message.RecipientType.TO, recps);
        }

        //CC recipients
        if (_ccRecipients != null)
        {
            recps = new InternetAddress[_ccRecipients.size()];
            _ccRecipients.copyInto(recps);
            msg.setRecipients(Message.RecipientType.CC, recps);
        }

        //BCC recipients
        if (_bccRecipients != null)
        {
            recps = new InternetAddress[_bccRecipients.size()];
            _bccRecipients.copyInto(recps);
            msg.setRecipients(Message.RecipientType.BCC, recps);
        }

        //Set the subject and send date
        msg.setSubject(_subject);
        msg.setSentDate( new Date() );


        // create the Multipart and its parts to it.  This is the contents of the email
        MimeMultipart mp = null;

        //create and add the body of the message. If the multipart messages are null,
        //then just add the message text. Otherwise, loop through the message parts and
        //add them
        if (_multipartMessages == null)
        {
            mp = new MimeMultipart();
            MimeBodyPart content = new MimeBodyPart();
            if (_contentType != null) content.setContent(_body, _contentType);
            else content.setText(_body);

            mp.addBodyPart(content);
        }
        else
        {
            mp = new MimeMultipart("alternative");
            int count = _multipartMessages.size();
            for (int i=0; i<count; i++)
            {
                mp.addBodyPart( (MimeBodyPart)_multipartMessages.elementAt(i) );
            }
        }

        //add any attachments
        if( _attachments != null && _attachments.size() > 0 )
        {
            Enumeration keys = _attachments.keys();
            while (keys.hasMoreElements())
            {
                MimeBodyPart bp = new MimeBodyPart();
                String name = (String) keys.nextElement();
                String filelocation = (String) _attachments.get(name);
                bp.setDataHandler(new DataHandler(new FileDataSource(filelocation)));
                bp.setFileName(name);

                mp.addBodyPart(bp);
            }
        }

        //add any headers
        if( _headers != null && _headers.size() > 0 )
        {
            Enumeration keys = _headers.keys();
            while (keys.hasMoreElements())
            {
                String name = (String)keys.nextElement();
                String value = (String)_headers.get(name);
                msg.setHeader(name, value);
            }
        }

        //Set the content type as multipart
        msg.setContent(mp);
        msg.saveChanges();

        // send the thing off
        boolean bSent = false;
        int sendTries = 0;
        while( !bSent && sendTries < getRetryCount() )
        {
            try
            {
                sendTries++;
                Transport.send(msg);
                bSent = true;
                //pause for 1 second before retry, if this wasn't the last try
                if ( sendTries < getRetryCount() )
                {
                    try
                    {
                        Thread.currentThread().sleep(getRetryDelay());
                    }
                    catch(java.lang.InterruptedException ie) { }
                }
            }
            catch(javax.mail.SendFailedException se)
            {
                //if we have reached the maximum number of tries, then throw the exception
                if ( sendTries == getRetryCount() )
                {
                    throw se;
                }
            }
        }
    }
}