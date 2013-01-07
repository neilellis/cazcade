package cazcade.common;

import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.RemoteComment;
import com.atlassian.jira.rpc.soap.client.RemoteFieldValue;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira_soapclient.SOAPSession;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.salt.SaltGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

//import com.newrelic.api.agent.NewRelic;

/**
 * @author Neil Ellis
 */
@SuppressWarnings({"UnusedDeclaration"})

public class Logger {

    public static final boolean USE_JIRA = false;
    @Nonnull
    private final org.apache.log4j.Logger logger;
    private static final Set<String> errorHashes = Collections.synchronizedSet(new HashSet<String>());

    @Nonnull
    private static final ThreadLocal<String> session = new ThreadLocal<String>();
    @Nonnull
    private static final ThreadLocal<String> username = new ThreadLocal<String>();
    @Nonnull
    private static final ThreadLocal<List> context = new ThreadLocal<List>() {
        @Nonnull
        @Override
        protected List initialValue() {
            return new ArrayList();
        }
    };

    @Nonnull
    private final StandardStringDigester digester;
    @Nonnull
    public static final XStream XSTREAM = new XStream();

    private Logger(@Nonnull final org.apache.log4j.Logger logger) {
        this.logger = logger;
        digester = new StandardStringDigester();
        digester.setSaltGenerator(new SaltGenerator() {
            public byte[] generateSalt(final int lengthBytes) {
                return logger.getName().getBytes();
            }

            public boolean includePlainSaltInEncryptionResults() {
                return false;
            }
        }
        );
    }


    private static boolean production;

    public static boolean isProduction() {
        return production;
    }

    static {
        try {
            production = System.getProperty("production") != null;
//        System.setProperty(org.apache.log4j.LogManager.DEFAULT_INIT_OVERRIDE_KEY, "true");
            final String log4JConfig = System.getProperty("log4j.configuration");
            if (log4JConfig == null) {
                final URL resource = Logger.class.getResource("/log4j.properties");
                if (resource != null) {
                    PropertyConfigurator.configure(resource);
                }
            } else {
                PropertyConfigurator.configure(log4JConfig);
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }

    }

    @Nonnull
    public static Logger getLogger(final String name) {
        return new Logger(org.apache.log4j.Logger.getLogger(name));
    }


    @Nonnull
    public static Logger getLogger(final Class clazz) {
        return new Logger(org.apache.log4j.Logger.getLogger(clazz));
    }


    public void assertLog(final boolean assertion, final String msg) {
        logger.assertLog(assertion, msg);
    }


    public void debug(final String message, @Nonnull final Object... params) {
        if (logger.isDebugEnabled()) {
            if (params.length == 1 && params[0] instanceof Throwable) {
                logger.debug(message, (Throwable) params[0]);
                writeToSessionLog(getPrefix() + message, "details");
                writeToSessionLog(ExceptionUtils.getFullStackTrace((Throwable) params[0]), "details");
            } else {
                logger.debug(getPrefix() + MessageFormat.format(message, params));
                writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "details");
            }
        }
    }


    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    private String getPrefix() {
        if (logger.isDebugEnabled() && !production) {
            final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
            for (final StackTraceElement stackTraceElement : stackTraceElements) {
                final String className = stackTraceElement.getClassName();
                if (!className.equals(Logger.class.getCanonicalName())) {
                    final String name = className.substring(className.lastIndexOf('.') + 1);
                    return MessageFormat.format("{0}:{1}({2}) ",
                            name,
                            stackTraceElement.getMethodName(),
                            stackTraceElement.getLineNumber()
                    );
                }
            }
            return "NO PREFIX FOUND : ";
        } else {
            return "";
        }
    }


    public void debug(final Throwable t, final String message, final Object... params) {
        if (
                logger.isDebugEnabled()) {
            logger.debug(message, t);
            logger.debug(getPrefix() + MessageFormat.format(message, params));
            writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "details");
            writeToSessionLog(ExceptionUtils.getFullStackTrace(t), "details");
        }
    }

    public void debug(final Throwable t, final String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, t);
            logger.debug(getPrefix() + message);
            writeToSessionLog(getPrefix() + message, "details");
            writeToSessionLog(ExceptionUtils.getFullStackTrace(t), "details");
        }
    }

    public void error(@Nonnull final Throwable t) {
        error(t, "{0}", t.getMessage());
    }


    public void error(final String message, @Nonnull final Object... params) {
        if (logger.isEnabledFor(Level.ERROR)) {
            if (params.length == 1 && params[0] instanceof Throwable) {
                error((Throwable) params[0], message);
            } else {
                logger.error(getPrefix() + MessageFormat.format(message, params));
                writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "error", "details");
            }
        }
    }


    public void error(@Nonnull final Throwable t, final String message, final Object... params) {
        if (logger.isEnabledFor(Level.ERROR)) {
            logger.error(getPrefix() + MessageFormat.format(message, params), t);
            writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "error", "details");
            writeToSessionLog(ExceptionUtils.getFullStackTrace(t), "error", "details");
            if (CommonConstants.IS_PRODUCTION && isReportableError(t)) {
                notifyOfError(t, MessageFormat.format(message, params));
//                NewRelic.noticeError(t);
            } else {
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                } else {
                    throw new RuntimeException(t);
                }
//                System.exit(-1);
            }
        }
    }

    private boolean isReportableError(Throwable t) {
        final String name = t.getClass().getName();
        return  ! "org.mortbay.jetty.EofException!".equals(name) &&
                ! "org.eclipse.jetty.io.EofException".equals(name) &&
                ! "java.lang.InterruptedException".equals(name) &&
                ! "org.eclipse.jetty.io.RuntimeIOException".equals(name);
    }


    public void fatal(@Nonnull final Throwable t) {
        error(t);
    }


    public void fatal(final String message, final Object... params) {
        error(message, params);
    }


    public void fatal(@Nonnull final Throwable t, final String message, final Object... params) {
        error(t, message, params);
    }


    public Level getLevel() {
        return logger.getLevel();
    }


    public String getName() {
        return logger.getName();
    }


    public Category getParent() {
        return logger.getParent();
    }


    public void info(final String message, @Nonnull final Object... params) {
        if (logger.isEnabledFor(Priority.INFO)) {
            if (params.length == 1 && params[0] instanceof Throwable) {
                logger.info(message, (Throwable) params[0]);
                writeToSessionLog(getPrefix() + message, "details");
                writeToSessionLog(ExceptionUtils.getFullStackTrace((Throwable) params[0]), "details");
            } else {
                writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "details");
                logger.info(getPrefix() + MessageFormat.format(message, params));
            }
        }
    }


    public void info(final Throwable t, final String message, final Object... params) {
        if (logger.isEnabledFor(Priority.INFO)) {
            logger.info(message, t);
            logger.info(getPrefix() + MessageFormat.format(message, params));
            writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "details");
            writeToSessionLog(ExceptionUtils.getFullStackTrace(t), "details");

        }
    }


    public void warn(final String message, @Nonnull final Object... params) {
        if (logger.isEnabledFor(Priority.WARN)) {
            if (params.length == 1 && params[0] instanceof Throwable) {
                logger.warn(message, (Throwable) params[0]);
                writeToSessionLog(getPrefix() + message, "error", "details");
                writeToSessionLog(ExceptionUtils.getFullStackTrace((Throwable) params[0]), "error", "details");
            } else {
                logger.warn(getPrefix() + MessageFormat.format(message, params));
                writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "error", "details");
            }
        }
    }


    public void warn(final Throwable t, final String message, final Object... params) {
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(message, t);
            logger.warn(getPrefix() + MessageFormat.format(message, params));
            writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "error", "details");
            writeToSessionLog(ExceptionUtils.getFullStackTrace(t), "error", "details");

        }
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public void session(@Nonnull final String message) {
        try {
            session(message.getBytes(CommonConstants.STRING_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }


    public void session(final byte[] message) {
        try {
            debug("Session: {0}", new String(message, CommonConstants.STRING_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
        writeToSessionLog(message, "session");
    }

    private void writeToSessionLog(@Nonnull final String message, final String... logTypes) {
        try {
            writeToSessionLog(message.getBytes(CommonConstants.STRING_ENCODING), logTypes);
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

    }

    private void writeToSessionLog(final byte[] message, @Nonnull final String... logTypes) {
        if (session.get() != null) {
            try {
                final File parent = getSessionLogDirectory();
                assert parent != null;
                parent.mkdirs();
                for (final String logType : logTypes) {
                    final File file = new File(parent, logType + ".txt");
                    final FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    IOUtils.write(message, fileOutputStream);
                    IOUtils.write("\n", fileOutputStream);
                    IOUtils.closeQuietly(fileOutputStream);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nullable
    private File getSessionLogDirectory() {
        if (session.get() == null) {
            return null;
        } else {
            final Calendar today = Calendar.getInstance();
            return new File(CommonConstants.DATASTORE_SESSION_LOGS +
                    "/" +
                    today.get(Calendar.DAY_OF_MONTH) +
                    "." +
                    (today.get(Calendar.MONTH) +
                            1) +
                    "." +
                    today.get(Calendar.YEAR) +
                    "/" +
                    (username.get() == null ? "" : username.get()), session.get()
            );

        }
    }

    public void setSession(@Nullable final String sessionId, @Nullable final String username) {
        if (sessionId == null) {
            session.set(null);
        } else {
            session.set(sessionId);
        }
        if (username == null) {
            Logger.username.set(null);
        } else {
            Logger.username.set(username);
        }
    }

    public void clearSession() {
        session.set(null);
        username.set(null);
    }


    public String hash(final String message) {

        return digester.digest(message);

    }

    public void notifyOfError(@Nonnull final Throwable t, final String message) {
        String hashStr = t.getClass().getName();
        final StackTraceElement[] stackTraceElements = t.getStackTrace();
        for (final StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement.getClassName().startsWith("cazcade")) {
                hashStr = hashStr + stackTraceElement.getClassName() + stackTraceElement.getMethodName();
            }

        }
        System.out.println("Hash String " + hashStr);
        final String hash = hash(hashStr);
        String location = "unknown location";
        for (final StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement.getClassName().startsWith("cazcade")) {
                location = stackTraceElement.getClassName() +
                        "." +
                        stackTraceElement.getMethodName() +
                        "(...) : " +
                        stackTraceElement.getLineNumber();
            }

        }

        final String summary = StringUtils.abbreviate(
                "BUG (SERVER)" + message + "   " + t.getClass().getSimpleName() + ": '" + t.getMessage() + "' at " + location + ")",
                250
        );
        String contextStr = "\n(No context available)\n";
//        if (context != null) {
//            contextStr = "\nContext:\n" + XSTREAM.toXML(context.get()) + "\n";
//        }
        final String description = "Automatically logged exception for " +
                username.get() +
                " (session='" +
                session.get() +
                "').\n\n" +
                message +
                "\n" +
                ExceptionUtils.getFullStackTrace(t)
                +
                contextStr;

        if (errorHashes.contains(hash)) {
            return;
        }

        errorHashes.add(hash);

        if (USE_JIRA) {
            sendToJira(message, hash, summary, description, "vortex");
        } else {
            send("FAO: Neil Only - Auto Bug Report - " + hash, description);
        }


    }

    public void sendToJira(final String message, final String hash, final String summary, final String description,
                           final String component) {
        sendToJira(message, hash, summary, description, component, null, null);
    }

    public void sendToJira(final String message, final String hash, final String summary, final String description,
                           final String component, @Nullable final byte[] attachment, @Nullable final String filename) {


        //reduce chances of duplicate JIRA spam.
        if (errorHashes.contains(hash)) {
            return;
        }

        errorHashes.add(hash);
        try {
            final SOAPSession soapSession;
            final JiraSoapService jira;
            soapSession = new SOAPSession(new URL("http://jira.cazcade.com/rpc/soap/jirasoapservice-v2"));
            soapSession.connect("neilellis", "password");
            jira = soapSession.getJiraSoapService();
            final RemoteIssue[] issues = jira.getIssuesFromJqlSearch(soapSession.getAuthenticationToken(),
                    "project = CAZCADE AND description ~ '" + hash + "'", 10
            );
            if (issues.length == 0) {
                final RemoteIssue issue = new RemoteIssue();
                issue.setSummary(summary);
                issue.setReporter("neilellis");
                issue.setAssignee("neilellis");
                issue.setType("1");
                issue.setProject("CAZCADE");
                issue.setDescription(description + "\n HASH=" + hash + "\n");
                final RemoteIssue remoteIssue = jira.createIssue(soapSession.getAuthenticationToken(), issue);
                logger.info("Created issue: " + remoteIssue.getKey());
                final File sessionLogDirectory = getSessionLogDirectory();
                if (sessionLogDirectory != null) {
                    try {
                        //create a ZipOutputStream to zip the data to
                        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        final ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream);
                        //assuming that there is a directory named inFolder (If there
                        //isn't create one) in the same directory as the one the code runs from,
                        //call the zipDir method
                        DirZip.zipDir(sessionLogDirectory.getAbsolutePath(), zos, false);
                        //close the stream
                        zos.close();
                        logger.debug("Error report size was " + byteArrayOutputStream.toByteArray().length);
                        jira.addBase64EncodedAttachmentsToIssue(soapSession.getAuthenticationToken(), remoteIssue.getKey(),
                                new String[]{"session.zip"}, new String[]{new String(
                                new Base64().encode(byteArrayOutputStream.toByteArray()), CommonConstants.STRING_ENCODING
                        )}
                        );
                        if (attachment != null) {
                            jira.addBase64EncodedAttachmentsToIssue(soapSession.getAuthenticationToken(), remoteIssue.getKey(),
                                    new String[]{filename}, new String[]{new String(
                                    new Base64().encode(attachment), CommonConstants.STRING_ENCODING
                            )}
                            );
                        }
                    } catch (Exception e) {
                        logger.error(e, e);
                    }
                }
            } else {
                for (final RemoteIssue issue : issues) {
                    final Calendar yesterday = Calendar.getInstance();
                    yesterday.add(Calendar.DATE, -1);
                    final String comment = "Logged again for " +
                            username.get() +
                            " (session='" +
                            session.get() +
                            "', message " +
                            message +
                            ").\n" +
                            description +
                            "\n";
                    //If it's closed - reopen.
                    if ("6".equals(issue.getStatus())) {
                        jira.progressWorkflowAction(soapSession.getAuthenticationToken(), issue.getKey(), "3",
                                new RemoteFieldValue[]{new RemoteFieldValue("comment", new String[]{comment})}
                        );
                    } else {
                        //Has it been updated since yesterday, if not add comment
                        if (issue.getUpdated() == null || issue.getUpdated().before(yesterday)) {

                            //If it was resolved yesterday, clearly it isn't resolved now. So reopen.
                            if ("5".equals(issue.getStatus())) {
                                jira.progressWorkflowAction(soapSession.getAuthenticationToken(), issue.getKey(), "3",
                                        new RemoteFieldValue[]{new RemoteFieldValue("comment",
                                                new String[]{comment}
                                        )}
                                );
                            }
                            final RemoteComment remoteComment = new RemoteComment();
                            remoteComment.setBody(comment);
                            jira.addComment(soapSession.getAuthenticationToken(), issue.getKey(), remoteComment);
                        }
                    }

//                    jira.updateIssue(soapSession.getAuthenticationToken(), issue.getKey(), new RemoteFieldValue[]{new RemoteFieldValue("votes", new String[]{String.valueOf(issue.getVotes() + 1L)})});
                    logger.info("Updated issue: " + issue.getKey());
                }
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
    }


    /**
     * "send" method to send the message.
     */

    public static void send(final String subject, final String body) {
        try {


            final String host = "localhost";
            final String to = "support@cazcade.zendesk.com";
            final String from = "neil@cazcade.com";

            final boolean sessionDebug = false;
            final Properties props = System.getProperties();
            props.setProperty("mail.host", host);
            props.setProperty("mail.transport.protocol", "smtp");
//            props.put("mail.smtp.auth", "false");
            final Session mailSession = Session.getDefaultInstance(props, null);
            mailSession.setDebug(sessionDebug);
            final Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, "Cazcade Bug Reporter"));
            final InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(body);
            msg.saveChanges();
            final Transport transport = mailSession.getTransport("smtp");
            transport.connect(host, "20d930a8-c079-43f6-9022-880156538a40", "20d930a8-c079-43f6-9022-880156538a40");
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addContext(final Object o) {
        context.get().add(o);
    }

    public void clearContext() {
        context.get().clear();
    }
}

