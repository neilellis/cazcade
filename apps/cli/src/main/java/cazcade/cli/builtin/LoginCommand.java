/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.CreateSessionRequest;
import cazcade.liquid.api.request.VisitPoolRequest;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public class LoginCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(LoginCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull @Override
    public String getDescription() {
        return "Login to the server.";
    }

    @Nonnull
    public String getName() {
        return "login";
    }


    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        if (args.length < 2) {
            System.err.println("You must specify the username and password");
            return "";
        }
        //        Principal principal = securityProvider.doAuthentication(username, password);
        //        if (principal == null) {
        //            return null;
        //        }
        final String username = args[0];
        final String password = args[1];
        final LiquidMessage response = shellSession.getDataStore()
                                                   .process(new CreateSessionRequest(new LURI(LiquidURIScheme.alias, "cazcade:"
                                                                                                                          + username), new ClientApplicationIdentifier("Shell Client", "123", "UNKNOWN")));
        log.debug(LiquidXStreamFactory.getXstream().toXML(response));
        final LiquidUUID sessionId = response.response().id();
        if (response.response().is(Types.T_SESSION)) {
            final SessionIdentifier identity = new SessionIdentifier(username, sessionId);
            final LiquidMessage visitPoolResponse = shellSession.getDataStore()
                                                                .process(new VisitPoolRequest(identity, new LURI(
                                                                        "pool:///people/"
                                                                        + username)));
            final TransferEntity visitPoolEntity = visitPoolResponse.response();
            shellSession.setCurrentPool(visitPoolEntity);
            shellSession.setIdentity(identity);
            log.info("Logged in with session id of {0}", sessionId);
        } else {
            throw new RuntimeException("Unexpected result " + response.response().type());
        }
        return sessionId.toString();
    }

}
