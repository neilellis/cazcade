package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.RetrievePoolRequest;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Neil Ellis
 */

public class ListPoolCommand extends AbstractShortLivedCommand {
    @Nonnull
    private final static Logger log = Logger.getLogger(ListPoolCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "List contents of a pool";
    }

    @Nonnull
    public String getName() {
        return "ls";
    }

    @Nonnull
    public String run(@Nonnull final String[] args, @Nonnull ShellSession shellSession) throws Exception {


        String pool;
        if (args.length > 0) {
            pool = args[0];
        } else {
            pool = "";
        }
        LiquidURI poolURI;
        if (pool.isEmpty()) {
            poolURI = shellSession.getCurrentPool().getURI();
        } else {
            poolURI = CommandSupport.resolvePoolOrObject(shellSession, pool);
        }
        LiquidMessage response = shellSession.getDataStore().process(new RetrievePoolRequest(shellSession.getIdentity(), poolURI, LiquidRequestDetailLevel.TITLE_AND_NAME, true, false));
        final LSDEntity listPoolEntity = response.getResponse();
        final List<LSDEntity> subEntities = listPoolEntity.getSubEntities(LSDAttribute.CHILD);
//        System.out.println(visitPoolResponseEntity);
        String result = "";
        for (LSDEntity subEntity : subEntities) {
            final String name = subEntity.getAttribute(LSDAttribute.NAME);
            System.out.println("" + name);
            result = result + " ";
        }
        return result;
    }


}
