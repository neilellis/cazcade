package cazcade.vortex.widgets.client.stream.chat;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ChatParser {

    public static final List<String> DM_ALIASES = Arrays.asList("d", "direct", "dm", "privmsg", "w", "whisper", "t", "tell");

    public boolean parse(String text) {
        final String[] args = text.substring(1).split(" ");
        if (DM_ALIASES.contains(args[0])) {
            if(args.length < 2) {
                return false;
            }
            LSDSimpleEntity messageEntity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.TEXT_MESSAGE);
            messageEntity.setAttribute(LSDAttribute.TEXT_EXTENDED, remainder(text, 2));
            BusFactory.getInstance().send(new SendRequest(messageEntity, args[1]), new AbstractResponseCallback<SendRequest>() {
                @Override
                public void onSuccess(SendRequest message, SendRequest response) {
                }
            });
            return true;
        }
        return false;
    }

    private String remainder(String text, int args) {
        int pos= -1;
        for(int i= 0; i < args; i++) {
            pos= text.indexOf(' ', pos+1);
        }
        return text.substring(pos+1);
    }
}
