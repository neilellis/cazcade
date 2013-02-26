/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.cli.commands;

import cazcade.cli.ShellSession;
import cazcade.fountain.common.service.ServiceStateMachine;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A Job is a generic course grained code block which can be executed periodically and takes arguments.
 *
 * @author Neil Ellis
 */

public interface Command extends ServiceStateMachine {

    boolean isShortLived();

    @Nonnull Options getOptions();

    @Nonnull String getDescription();

    @Nonnull String getName();

    @Nullable String getShortName();

    /**
     * A positive interval indicates a repeating job which should be repeated every n seconds. A negative interval
     * indicates a once only execution job.
     *
     * @return interval in seconds.
     */
    long getIntervalSeconds();

    /**
     * The arguments given specifically to this job e.g. run.sh "export -p 1000" whould pass -p 1000 as arguments to the
     * job named "export".
     *
     * @param args         arguments.
     * @param shellSession
     * @throws Exception if something goes wrong.
     */
    @Nullable String run(String[] args, ShellSession shellSession) throws Exception;

    /**
     * How long in seconds before this job should be executed.
     *
     * @return initial delayAsync in seconds.
     */
    long getInitialDelaySeconds();

    void stop();

    void init(CommandInitContext context) throws Exception;
}
