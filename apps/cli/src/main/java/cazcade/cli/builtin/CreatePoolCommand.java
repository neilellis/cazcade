package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
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
    private final static Logger log = Logger.getLogger(CreatePoolCommand.class);

    private Map<String, String> attributes;

    public CreatePoolCommand(Map attributes) {
        this.attributes = attributes;
    }

    public CreatePoolCommand() {
    }

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Create pool";
    }

    @Nonnull
    public String getName() {
        return "mkdir";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("You must specify the new pool's name");
            return "";
        }

        String pool = args[0];
        LiquidURI poolURI;
        LiquidMessage response = shellSession.getDataStore().process(new CreatePoolRequest(shellSession.getIdentity(),
                shellSession.getCurrentPool().getURI(), pool, pool, pool, 0, 0));
        final LSDEntity responseEntity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(responseEntity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        } else {
            if (attributes != null) {
                for (Map.Entry<String, String> entry : attributes.entrySet()) {
                    responseEntity.setAttribute(LSDAttribute.valueOf(entry.getKey()), entry.getValue());
                }
                LiquidMessage response2 = shellSession.getDataStore().process(new UpdatePoolRequest(shellSession.getIdentity(), responseEntity.getUUID(), responseEntity));
                if (response2.getState() != LiquidMessageState.SUCCESS) {
                    System.err.println(response2.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                    return null;
                }
            }
            return responseEntity.getURI().toString();
        }
    }


}
