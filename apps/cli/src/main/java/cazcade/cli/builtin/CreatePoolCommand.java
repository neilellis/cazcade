/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.CreatePoolRequest;
import cazcade.liquid.api.request.UpdatePoolRequest;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class CreatePoolCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(CreatePoolCommand.class);

    private Map<String, String> attributes;

    public CreatePoolCommand(final Map attributes) {
        super();
        this.attributes = attributes;
    }

    public CreatePoolCommand() {
        super();
    }

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull @Override
    public String getDescription() {
        return "Create pool";
    }

    @Nonnull
    public String getName() {
        return "mkdir";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("You must specify the new pool's name");
            return "";
        }

        final String pool = args[0];
        LiquidURI poolURI;
        final LiquidMessage response = shellSession.getDataStore()
                                                   .process(new CreatePoolRequest(shellSession.getIdentity(), shellSession.getCurrentPool()
                                                                                                                          .uri(), pool, pool, pool, 0, 0));
        final TransferEntity responseEntity = response.response();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(responseEntity.$(Dictionary.DESCRIPTION));
            return null;
        } else {
            if (attributes != null) {
                for (final Map.Entry<String, String> entry : attributes.entrySet()) {
                    responseEntity.$(Attribute.valueOf(entry.getKey()), entry.getValue());
                }
                final LiquidMessage response2 = shellSession.getDataStore()
                                                            .process(new UpdatePoolRequest(shellSession.getIdentity(), responseEntity
                                                                    .id(), responseEntity));
                if (response2.getState() != LiquidMessageState.SUCCESS) {
                    System.err.println(response2.response().$(Dictionary.DESCRIPTION));
                    return null;
                }
            }
            return responseEntity.uri().toString();
        }
    }


}
