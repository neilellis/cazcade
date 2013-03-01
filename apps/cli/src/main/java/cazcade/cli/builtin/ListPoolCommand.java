/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.TransferEntityCollection;
import cazcade.liquid.api.request.RetrievePoolRequest;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Neil Ellis
 */

public class ListPoolCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(ListPoolCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull @Override
    public String getDescription() {
        return "List contents of a pool";
    }

    @Nonnull
    public String getName() {
        return "ls";
    }

    @Nonnull
    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {


        final String pool;
        if (args.length > 0) {
            pool = args[0];
        } else {
            pool = "";
        }
        final LURI poolURI;
        if (pool.isEmpty()) {
            poolURI = shellSession.getCurrentPool().uri();
        } else {
            poolURI = CommandSupport.resolvePoolOrObject(shellSession, pool);
        }
        final LiquidMessage response = shellSession.getDataStore()
                                                   .process(new RetrievePoolRequest(shellSession.getIdentity(), poolURI, RequestDetailLevel.TITLE_AND_NAME, true, false));
        final TransferEntity listPoolEntity = response.response();
        final TransferEntityCollection<? extends TransferEntity> subEntities = listPoolEntity.children();
        //        System.out.println(visitPoolResponseEntity);
        String result = "";
        for (final Entity subEntity : subEntities) {
            final String name = subEntity.$(Dictionary.NAME);
            System.out.println(name);
            result = result + " ";
        }
        return result;
    }


}
