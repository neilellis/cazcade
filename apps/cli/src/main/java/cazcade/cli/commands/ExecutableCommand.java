package cazcade.cli.commands;

import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public interface ExecutableCommand {

    @Nullable
    String execute() throws Exception;
}
