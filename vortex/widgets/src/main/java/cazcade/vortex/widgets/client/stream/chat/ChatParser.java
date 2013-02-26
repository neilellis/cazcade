/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream.chat;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ChatParser {

    public static final List<String> DM_ALIASES = Arrays.asList("d", "direct", "dm", "privmsg", "w", "whisper", "t", "tell");

    public boolean parse(@Nonnull final String text) {
        final String[] args = text.substring(1).split(" ");
        if (DM_ALIASES.contains(args[0])) {
            if (args.length < 2) {
                return false;
            }
            final TransferEntity messageEntity = SimpleEntity.create(Types.T_TEXT_MESSAGE);
            messageEntity.$(Dictionary.TEXT_EXTENDED, remainder(text, 2));
            BusFactory.get().send(new SendRequest(messageEntity, args[1]), new AbstractResponseCallback<SendRequest>() {
                @Override
                public void onSuccess(final SendRequest message, final SendRequest response) {
                }
            });
            return true;
        }
        return false;
    }

    private String remainder(@Nonnull final String text, final int args) {
        int pos = -1;
        for (int i = 0; i < args; i++) {
            pos = text.indexOf(' ', pos + 1);
        }
        return text.substring(pos + 1);
    }
}
