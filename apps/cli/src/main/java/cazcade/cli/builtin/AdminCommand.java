/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.request.AdminCommandRequest;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class AdminCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(AdminCommand.class);


    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull @Override
    public String getDescription() {
        return "Send admin request to server.";
    }

    @Nonnull
    public String getName() {
        return "admin";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("You must specify at least a command name");
            return "";
        }
        final LiquidMessage response = shellSession.getDataStore()
                                                   .process(new AdminCommandRequest(null, shellSession.getIdentity(), args));
        final Entity responseEntity = response.response();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(responseEntity.$(Dictionary.DESCRIPTION));
            return null;
        }
        return "";
    }


}
