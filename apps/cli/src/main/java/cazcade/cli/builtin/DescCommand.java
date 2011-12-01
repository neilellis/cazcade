package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.RetrievePoolObjectRequest;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public class DescCommand extends AbstractShortLivedCommand {
    @Nonnull
    private final static Logger log = Logger.getLogger(DescCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Describe a pool object (this includes pools).";
    }

    @Nonnull
    public String getName() {
        return "desc";
    }

    public String run(@Nonnull final String[] args, @Nonnull ShellSession shellSession) throws Exception {


        String object;
        if (args.length > 0) {
            object = args[0];
        } else {
            object = "";
        }
        LiquidURI poolURI;
        if (object.isEmpty()) {
            poolURI = shellSession.getCurrentPool().getURI();
        } else {
            poolURI = CommandSupport.resolvePoolOrObject(shellSession, object);
        }
        LiquidMessage response = shellSession.getDataStore().process(new RetrievePoolObjectRequest(shellSession.getIdentity(), poolURI, true));
        System.err.println(poolURI);
        final LSDEntity entity = response.getResponse();
        System.out.println(entity);
        return entity.getUUID().toString();
    }


}
