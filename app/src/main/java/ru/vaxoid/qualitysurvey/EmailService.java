package ru.vaxoid.qualitysurvey;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.PreencodedMimeBodyPart;

/**
 * @author Ako Tulu & Mathias Conradt
 */
public class EmailService
{
    public final static String CLIENT_NAME = "Android Jakarta Mail";

    private String host = null;
    private Integer port = null;
    private boolean startTls = false;
    private boolean enableSelfSigned = true;
    private javax.mail.Authenticator auth = null;
    private InternetAddress from = null;
    private InternetAddress[] toList = null;
    private InternetAddress[] ccList = null;
    private InternetAddress[] bccList = null;
    private Address[] replyToList = null;

    private String subject = null;
    private String txtBody = null;
    private String htmlBody = null;
    private List< Attachment > attachments = new ArrayList<>();

    private PropertyInjector injector = null;

    /**
     * Attachment
     */
    public static class Attachment
    {
        protected final DataSource dataSource;

        public Attachment( final DataSource dataSource )
        {
            this.dataSource = dataSource;
        }

        /**
         * BUG: InputStream has to be new instance every call.
         * Stream is read to retrieve Content-Type and by SMTP write to socket,
         * but stream is read once, reading twice will result in empty result.
         *
         * To retrive Content-Type, library has to copy the stream (be a middleman) or
         * extend itself with a peak command.
         *
         * public InputStream getInputStream()
         */
        DataSource getDataSource()
        {
            return dataSource;
        }
    }

    /**
     * Authenticator
     */
    public static class Authenticator extends javax.mail.Authenticator
    {
        private final String username;
        private final String password;

        public Authenticator( final String username, final String password )
        {
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication( username, password );
        }
    }

    /**
     * PropertyInjector, if custom properties are needed to be set.
     */
    public interface PropertyInjector
    {
        void inject( Properties properties );
    }

    /**
     * Callback, success if exception is null
     */
    public interface Callback
    {
        void done( @Nullable Exception e );

        static void handle( @Nullable Callback c, Exception e )
        {
            if ( c != null )
            {
                new Handler( Looper.getMainLooper() ).post( () -> c.done( e ));
            }
        }
    }

    /**
     * EmailService
     */
    public EmailService( final String host, final Integer port )
    {
        this.host = host;
        this.port = port;
    }

    /**
     * EmailService
     */
    public EmailService()
    {
    }

    /**
     * Send e-mail, sync or async defined by @async var
     *
     * @throws MessagingException, invalid parameter
     * @throws AddressException,   obsecure string
     */
    public void send( boolean async, @Nullable Callback callback )
    {
        if ( async )
        {
            AsyncTask.execute( () -> send( callback ));
        }
        else
        {
            send( callback );
        }
    }

    /**
     * Send an e-mail
     *
     * @throws MessagingException, invalid parameter
     * @throws AddressException,   obsecure string
     */
    public void send( @Nullable Callback callback )
    {
        try
        {
            Properties props = new Properties();

            Session session;
            Log.i("MAIL", "Старт отправки");
            if ( getAuth() != null )
            {
                props.put( "mail.smtp.auth", "true" );

                session = Session.getDefaultInstance( props, getAuth() );
            }
            else
            {
                session = Session.getDefaultInstance( props, null );
            }

            // create message
            Log.i("MAIL", "create message");
            Message msg = new javax.mail.internet.MimeMessage( session );

            msg.setFrom( getFrom() );
            msg.setSentDate( Calendar.getInstance().getTime() );
            msg.setRecipients( javax.mail.Message.RecipientType.TO, getToList() );
            msg.setRecipients( javax.mail.Message.RecipientType.CC, getCcList() );
            msg.setRecipients( javax.mail.Message.RecipientType.BCC, getBccList() );
            msg.setReplyTo( getReplyToList() );

            // set header
            Log.i("MAIL", "set header");
            msg.addHeader( "X-Mailer", CLIENT_NAME );
            msg.addHeader( "Precedence", "bulk" );

            msg.setSubject( getSubject() );

            // set body message
            Log.i("MAIL", "set body message");
            Multipart mp = new MimeMultipart();

            MimeBodyPart bodyMsg = new MimeBodyPart();
            bodyMsg.setText( getTxtBody(), "iso-8859-1" );
            bodyMsg.setContent( getHtmlBody(), "text/html; charset=UTF-8" );

            mp.addBodyPart( bodyMsg );

            // set attachments if any
            final List< Attachment > list = getAttachments();
            if ( list.size() > 0 )
            {
                for ( int i = 0; i < list.size(); i++ )
                {
                    Attachment a = list.get( i );

                    BodyPart att = new PreencodedMimeBodyPart( "base64" );

                    att.setFileName( a.getDataSource().getName() );
                    att.setDataHandler( new DataHandler( a.getDataSource() ) );

                    mp.addBodyPart( att );
                }
            }
            msg.setContent( mp );

            if ( getInjector() != null )
            {
                getInjector().inject( props );
            }
            // set the host smtp address
            Log.i("MAIL", "set the host smtp address");
            props.put( "mail.smtp.host", getHost() );
            props.put( "mail.smtp.port", getPort() );

            props.put( "mail.user", getFrom() );

            if ( isStartTls() )
            {
                props.put( "mail.smtp.starttls.enable", "true" );
            }

            if ( isEnableSelfSigned() )
            {
                props.put( "mail.smtp.ssl.trust", getHost() );
            }

            props.put( "mail.mime.charset", "UTF-8" );

            // send it
            Log.i("MAIL", "send it");
            javax.mail.Transport.send( msg );

            Callback.handle( callback, null );
        }
        catch ( Exception e )
        {
            Log.i("MAIL", e.toString());
            Callback.handle( callback, e );
        }
    }

    /**
     * Parse comma separated string into @javax.mail.internet.InternetAddress list
     */
    @NonNull
    public static InternetAddress[] parseAddress( final String address )
            throws AddressException
    {
        List< InternetAddress > list = new ArrayList<>();

        if ( address != null && !"".equals( address ) )
        {
            StringTokenizer st = new StringTokenizer( address, "," );

            while ( st.hasMoreTokens() )
            {
                list.add( new InternetAddress( st.nextToken() ) );
            }
        }
        return list.toArray( new InternetAddress[ list.size() ] );
    }

    /**
     * Resets internals for reuse
     */
    public EmailService reset()
    {
        this.from = null;
        this.toList = null;
        this.ccList = null;
        this.bccList = null;
        this.replyToList = null;

        this.subject = null;
        this.txtBody = null;
        this.htmlBody = null;
        this.attachments = new ArrayList<>();

        return this;
    }

    public String getHost()
    {
        return host;
    }

    public EmailService setHost( final String host )
    {
        this.host = host;

        return this;
    }

    public Integer getPort()
    {
        return port;
    }

    public EmailService setPort( final String port )
    {
        this.port = Integer.parseInt( port );

        return this;
    }

    public EmailService setPort( final int port )
    {
        this.port = port;

        return this;
    }

    public boolean isEnableSelfSigned()
    {
        return enableSelfSigned;
    }

    public EmailService setEnableSelfSigned( boolean enableSelfSigned )
    {
        this.enableSelfSigned = enableSelfSigned;

        return this;
    }

    public boolean isStartTls()
    {
        return startTls;
    }

    public EmailService setStartTls( boolean startTls )
    {
        this.startTls = startTls;

        return this;
    }

    public javax.mail.Authenticator getAuth()
    {
        return auth;
    }

    public EmailService setAuth( final javax.mail.Authenticator auth )
    {
        this.auth = auth;

        return this;
    }

    public InternetAddress getFrom()
    {
        return from;
    }

    public EmailService setFrom( final String from ) throws AddressException
    {
        this.from = new InternetAddress( from );

        return this;
    }

    public EmailService setFrom( final InternetAddress from )
    {
        this.from = from;

        return this;
    }

    public InternetAddress[] getToList()
    {
        return toList;
    }

    public EmailService setToList( final String toList ) throws AddressException
    {
        return setToList( parseAddress( toList ) );
    }

    public EmailService setToList( final InternetAddress[] toList )
    {
        this.toList = toList;

        return this;
    }

    public InternetAddress[] getCcList()
    {
        return ccList;
    }

    public EmailService setCcList( final String ccList ) throws AddressException
    {
        return setCcList( parseAddress( ccList ) );
    }

    public EmailService setCcList( final InternetAddress[] ccList )
    {
        this.ccList = ccList;

        return this;
    }

    public InternetAddress[] getBccList()
    {
        return bccList;
    }

    public EmailService setBccList( final String bccList ) throws AddressException
    {
        return setBccList( parseAddress( bccList ) );
    }

    public EmailService setBccList( final InternetAddress[] bccList )
    {
        this.bccList = bccList;

        return this;
    }

    public Address[] getReplyToList()
    {
        return replyToList;
    }

    public EmailService setReplyToList( final Address[] replyTo )
    {
        this.replyToList = replyTo;

        return this;
    }

    public String getSubject()
    {
        return subject;
    }

    public EmailService setSubject( final String subject )
    {
        this.subject = subject;

        return this;
    }

    public String getTxtBody()
    {
        return txtBody;
    }

    public EmailService setTxtBody( final String txtBody )
    {
        this.txtBody = txtBody;

        return this;
    }

    public String getHtmlBody()
    {
        return htmlBody;
    }

    public EmailService setHtmlBody( final String htmlBody )
    {
        this.htmlBody = htmlBody;

        return this;
    }

    public List< Attachment > getAttachments()
    {
        return attachments;
    }

    public EmailService setAttachments( final Attachment attachment )
    {
        this.attachments.add( attachment );

        return this;
    }

    public EmailService setAttachments( final List< Attachment > attachments )
    {
        this.attachments = attachments;

        return this;
    }

    public PropertyInjector getInjector()
    {
        return injector;
    }

    public EmailService setInjector( final PropertyInjector injector )
    {
        this.injector = injector;

        return this;
    }

}