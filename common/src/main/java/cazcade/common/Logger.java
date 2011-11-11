package cazcade.common;

import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.RemoteComment;
import com.atlassian.jira.rpc.soap.client.RemoteFieldValue;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira_soapclient.SOAPSession;
import com.newrelic.api.agent.NewRelic;
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

/**
 * @author Neil Ellis
 */
@SuppressWarnings({"UnusedDeclaration"})

public class Logger {

    public static final boolean USE_JIRA = false;
    private final org.apache.log4j.Logger logger;
    private static Set<String> errorHashes = Collections.synchronizedSet(new HashSet<String>());

    private static ThreadLocal<String> session = new ThreadLocal<String>();
    private static ThreadLocal<String> username = new ThreadLocal<String>();
    private static ThreadLocal<List> context = new ThreadLocal<List>() {
        @Override
        protected List initialValue() {
            return new ArrayList();
        }
    };

    private StandardStringDigester digester;
    public static final XStream XSTREAM = new XStream();

    private Logger(final org.apache.log4j.Logger logger) {
        this.logger = logger;
        digester = new StandardStringDigester();
        digester.setSaltGenerator(new SaltGenerator() {
            public byte[] generateSalt(int lengthBytes) {
                return logger.getName().getBytes();
            }

            public boolean includePlainSaltInEncryptionResults() {
                return false;
            }
        });
    }


    private static boolean production;

    static {
        try {
            production = System.getProperty("production") != null;
//        System.setProperty(org.apache.log4j.LogManager.DEFAULT_INIT_OVERRIDE_KEY, "true");
            String log4JConfig = System.getProperty("log4j.configuration");
            if (log4JConfig == null) {
                URL resource = Logger.class.getResource("/log4j.properties");
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

    public static Logger getLogger(String name) {
        return new Logger(org.apache.log4j.Logger.getLogger(name));
    }


    public static Logger getLogger(Class clazz) {
        return new Logger(org.apache.log4j.Logger.getLogger(clazz));
    }


    public void assertLog(boolean assertion, String msg) {
        logger.assertLog(assertion, msg);
    }


    public void debug(String message, Object... params) {
        if (logger != null && logger.isDebugEnabled()) {
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
        if (logger != null && logger.isDebugEnabled() && !production) {
            StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                String className = stackTraceElement.getClassName();
                if (!className.equals(Logger.class.getCanonicalName())) {
                    String name = className.substring(className.lastIndexOf('.') + 1);
                    return MessageFormat.format("{0}:{1}({2}) ",
                            name,
                            stackTraceElement.getMethodName(),
                            stackTraceElement.getLineNumber());
                }
            }
            return "NO PREFIX FOUND : ";
        } else {
            return "";
        }
    }


    public void debug(Throwable t, String message, Object... params) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(message, t);
            logger.debug(getPrefix() + MessageFormat.format(message, params));
            writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "details");
            writeToSessionLog(ExceptionUtils.getFullStackTrace(t), "details");
        }
    }

    public void debug(Throwable t, String message) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(message, t);
            logger.debug(getPrefix() + message);
            writeToSessionLog(getPrefix() + message, "details");
            writeToSessionLog(ExceptionUtils.getFullStackTrace(t), "details");
        }
    }

    public void error(Throwable t) {
        error(t, "{0}", t.getMessage());
    }


    public void error(String message, Object... params) {
        if (logger != null && logger.isEnabledFor(Level.ERROR)) {
            if (params.length == 1 && params[0] instanceof Throwable) {
                error((Throwable) params[0], message);
            } else {
                logger.error(getPrefix() + MessageFormat.format(message, params));
                writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "error", "details");
            }
        }
    }


    public void error(Throwable t, String message, Object... params) {
        if (logger != null && logger.isEnabledFor(Level.ERROR)) {
            logger.error(getPrefix() + MessageFormat.format(message, params), t);
            writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "error", "details");
            writeToSessionLog(ExceptionUtils.getFullStackTrace(t), "error", "details");
            if (CommonConstants.IS_PRODUCTION) {
                NewRelic.noticeError(t);
                notifyOfError(t, MessageFormat.format(message, params));
            } else {
//                System.exit(-1);
            }
        }
    }


    public void fatal(Throwable t) {
        error(t);
    }


    public void fatal(String message, Object... params) {
        error(message, params);
    }


    public void fatal(Throwable t, String message, Object... params) {
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


    public void info(String message, Object... params) {
        if (logger != null && logger.isEnabledFor(Priority.INFO)) {
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


    public void info(Throwable t, String message, Object... params) {
        if (logger != null && logger.isEnabledFor(Priority.INFO)) {
            logger.info(message, t);
            logger.info(getPrefix() + MessageFormat.format(message, params));
            writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "details");
            writeToSessionLog(ExceptionUtils.getFullStackTrace(t), "details");

        }
    }


    public void warn(String message, Object... params) {
        if (logger != null && logger.isEnabledFor(Priority.WARN)) {
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


    public void warn(Throwable t, String message, Object... params) {
        if (logger != null && logger.isEnabledFor(Priority.WARN)) {
            logger.warn(message, t);
            logger.warn(getPrefix() + MessageFormat.format(message, params));
            writeToSessionLog(getPrefix() + MessageFormat.format(message, params), "error", "details");
            writeToSessionLog(ExceptionUtils.getFullStackTrace(t), "error", "details");

        }
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public void session(String message) {
        try {
            session(message.getBytes(CommonConstants.STRING_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }


    public void session(byte[] message) {
        try {
            debug("Session: {0}", new String(message, CommonConstants.STRING_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
        writeToSessionLog(message, "session");
    }

    private void writeToSessionLog(String message, String... logTypes) {
        try {
            writeToSessionLog(message.getBytes(CommonConstants.STRING_ENCODING), logTypes);
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

    }

    private void writeToSessionLog(byte[] message, String... logTypes) {
        if (session.get() != null) {
            try {
                File parent = getSessionLogDirectory();
                parent.mkdirs();
                for (String logType : logTypes) {
                    File file = new File(parent, logType + ".txt");
                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    IOUtils.write(message, fileOutputStream);
                    IOUtils.write("\n", fileOutputStream);
                    IOUtils.closeQuietly(fileOutputStream);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private File getSessionLogDirectory() {
        if (session.get() == null) {
            return null;
        } else {
            Calendar today = Calendar.getInstance();
            return new File(CommonConstants.DATASTORE_SESSION_LOGS + "/" + today.get(Calendar.DAY_OF_MONTH) + "." + (today.get(Calendar.MONTH) + 1) + "." + today.get(Calendar.YEAR) + "/" + (username.get() == null ? "" : username.get()), session.get());
        }
    }

    public void setSession(String sessionId, String username) {
        if (sessionId == null) {
            session.set(null);
        } else {
            session.set(sessionId);
        }
        if (username == null) {
            this.username.set(null);
        } else {
            this.username.set(username);
        }
    }

    public void clearSession() {
        session.set(null);
        username.set(null);
    }


    public String hash(String message) {

        return digester.digest(message);

    }

    public void notifyOfError(Throwable t, String message) {
        String hashStr = t.getClass().getName();
        StackTraceElement[] stackTraceElements = t.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement.getClassName().startsWith("cazcade")) {
                hashStr = hashStr + stackTraceElement.getClassName() + stackTraceElement.getMethodName();
            }

        }
        System.out.println("Hash String " + hashStr);
        String hash = hash(hashStr);
        String location = "unknown location";
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement.getClassName().startsWith("cazcade")) {
                location = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(...) : " + stackTraceElement.getLineNumber();
            }

        }

        String summary = StringUtils.abbreviate("BUG (SERVER)" + message + "   " + t.getClass().getSimpleName() + ": '" + t.getMessage() + "' at " + location + ")", 250);
        String contextStr = "\n(No context available)\n";
        if (context != null) {
            contextStr = "\nContext:\n" + XSTREAM.toXML(context.get()) + "\n";
        }
        String description = "Automatically logged exception for " + username.get() + " (session='" + session.get() + "').\n\n" + message + "\n" + ExceptionUtils.getFullStackTrace(t)
                + contextStr;

        if (USE_JIRA) {
            sendToJira(message, hash, summary, description, "vortex");
        } else {
            send("Auto Bug Report - " + hash, description);
        }


    }

    public void sendToJira(String message, String hash, String summary, String description, String component) {
        sendToJira(message, hash, summary, description, component, null, null);
    }

    public void sendToJira(String message, String hash, String summary, String description, String component, byte[] attachment, String filename) {


        //reduce chances of duplicate JIRA spam.
        if (errorHashes.contains(hash)) {
            return;
        }

        errorHashes.add(hash);
        try {
            SOAPSession soapSession;
            JiraSoapService jira;
            soapSession = new SOAPSession(new URL("http://jira.cazcade.com/rpc/soap/jirasoapservice-v2"));
            soapSession.connect("neilellis", "vipassana");
            jira = soapSession.getJiraSoapService();
            RemoteIssue[] issues = jira.getIssuesFromJqlSearch(soapSession.getAuthenticationToken(), "project = CAZCADE AND description ~ '" + hash + "'", 10);
            if (issues.length == 0) {
                RemoteIssue issue = new RemoteIssue();
                issue.setSummary(summary);
                issue.setReporter("neilellis");
                issue.setAssignee("neilellis");
                issue.setType("1");
                issue.setProject("CAZCADE");
                issue.setDescription(description + "\n HASH=" + hash + "\n");
                RemoteIssue remoteIssue = jira.createIssue(soapSession.getAuthenticationToken(), issue);
                logger.info("Created issue: " + remoteIssue.getKey());
                File sessionLogDirectory = getSessionLogDirectory();
                if (sessionLogDirectory != null) {
                    try {
                        //create a ZipOutputStream to zip the data to
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream);
                        //assuming that there is a directory named inFolder (If there
                        //isn't create one) in the same directory as the one the code runs from,
                        //call the zipDir method
                        DirZip.zipDir(sessionLogDirectory.getAbsolutePath(), zos, false);
                        //close the stream
                        zos.close();
                        logger.debug("Error report size was " + byteArrayOutputStream.toByteArray().length);
                        jira.addBase64EncodedAttachmentsToIssue(soapSession.getAuthenticationToken(), remoteIssue.getKey(), new String[]{"session.zip"}, new String[]{new String(new Base64().encode(byteArrayOutputStream.toByteArray()), CommonConstants.STRING_ENCODING)});
                        if (attachment != null) {
                            jira.addBase64EncodedAttachmentsToIssue(soapSession.getAuthenticationToken(), remoteIssue.getKey(), new String[]{filename}, new String[]{new String(new Base64().encode(attachment), CommonConstants.STRING_ENCODING)});
                        }
                    } catch (Exception e) {
                        logger.error(e, e);
                    }
                }
            } else {
                for (RemoteIssue issue : issues) {
                    Calendar yesterday = Calendar.getInstance();
                    yesterday.add(Calendar.DATE, -1);
                    String comment = "Logged again for " + username.get() + " (session='" + session.get() + "', message " + message + ").\n" + description + "\n";
                    //If it's closed - reopen.
                    if (issue.getStatus().equals("6")) {
                        jira.progressWorkflowAction(soapSession.getAuthenticationToken(), issue.getKey(), "3", new RemoteFieldValue[]{new RemoteFieldValue("comment", new String[]{comment})});
                    } else {
                        //Has it been updated since yesterday, if not add comment
                        if (issue.getUpdated() == null || issue.getUpdated().before(yesterday)) {

                            //If it was resolved yesterday, clearly it isn't resolved now. So reopen.
                            if (issue.getStatus().equals("5")) {
                                jira.progressWorkflowAction(soapSession.getAuthenticationToken(), issue.getKey(), "3", new RemoteFieldValue[]{new RemoteFieldValue("comment", new String[]{comment})});
                            }
                            RemoteComment remoteComment = new RemoteComment();
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

    public static void send(String subject, String body) {
        try {


            String host = "smtp.postmarkapp.com";
            String to = "support@boardcast.zendesk.com";
            String from = "support@boardcast.it";

            boolean sessionDebug = false;
            Properties props = System.getProperties();
            props.put("mail.host", host);
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            Session mailSession = Session.getDefaultInstance(props, null);
            mailSession.setDebug(sessionDebug);
            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, "Bug Reporter"));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(body);
            msg.saveChanges();
            Transport transport = mailSession.getTransport("smtp");
            transport.connect(host, "20d930a8-c079-43f6-9022-880156538a40", "20d930a8-c079-43f6-9022-880156538a40");
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addContext(Object o) {
        context.get().add(o);
    }

    public void clearContext() {
        context.get().clear();
    }
}

