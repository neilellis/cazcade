/*
* Copyright (c) 2009 Mangala Solutions Ltd, all rights reserved.
*/

package cazcade.cli;

import cazcade.cli.builtin.EchoCommand;
import cazcade.cli.builtin.HelpCommand;
import cazcade.cli.builtin.LogCommand;
import cazcade.cli.builtin.SleepCommand;
import cazcade.cli.commands.CommandFactory;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public class CommandFactoryBuilder {


    public static void build(@Nonnull final CommandFactory commandFactory) throws Exception {
        commandFactory.add(new HelpCommand());
        commandFactory.add(new LogCommand());
        commandFactory.add(new EchoCommand());
        commandFactory.add(new SleepCommand());
    }
}
