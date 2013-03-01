/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.MessageState;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.request.DeletePoolRequest;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class DeletePoolCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(DeletePoolCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull @Override
    public String getDescription() {
        return "Deletes a pool";
    }

    @Nonnull
    public String getName() {
        return "rmdir";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("rmdir <pool>");
        }
        final LURI poolURI = CommandSupport.resolvePoolOrObject(shellSession, args[0]);
        final LiquidMessage response = shellSession.getDataStore()
                                                   .process(new DeletePoolRequest(shellSession.getIdentity(), poolURI));
        final Entity entity = response.response();
        if (response.state() != MessageState.SUCCESS) {
            System.err.println(entity.$(Dictionary.DESCRIPTION));
            return null;
        }
        return entity.uri().toString();
    }


}
