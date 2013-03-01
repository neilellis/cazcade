/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class RenamePoolCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(RenamePoolCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull @Override
    public String getDescription() {
        return "Rename a pool";
    }

    @Nonnull
    public String getName() {
        return "rename";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {


        final String from;
        final String to;
        if (args.length < 2) {
            System.err.println("rename <from> <to>");
            return null;
        } else {
            from = args[0];
            to = args[1];
        }
        final LURI poolURI;
        poolURI = CommandSupport.resolvePoolOrObject(shellSession, from);
        return CommandSupport.alterPool(shellSession, poolURI, new CommandSupport.AlterEntityCallback() {
            @Nonnull @Override
            public TransferEntity alter(@Nonnull final TransferEntity entity) {
                entity.$(Dictionary.NAME, to);
                return entity;

            }
        });


    }


}
