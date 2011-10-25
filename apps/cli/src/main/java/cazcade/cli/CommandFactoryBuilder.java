/*
* Copyright (c) 2009 Mangala Solutions Ltd, all rights reserved.
*/

package cazcade.cli;

import cazcade.cli.builtin.*;
import cazcade.cli.builtin.HelpCommand;
import cazcade.cli.builtin.LogCommand;
import cazcade.cli.commands.*;

/**
 * @author Neil Ellis
 */

public class CommandFactoryBuilder {


    public static void build(CommandFactory commandFactory) throws Exception {
        commandFactory.add(new HelpCommand());
        commandFactory.add(new LogCommand());
        commandFactory.add(new EchoCommand());
        commandFactory.add(new SleepCommand());
    }
}
