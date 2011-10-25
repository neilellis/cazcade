/*
 * Copyright (c) 2009 Mangala Solutions Ltd, all rights reserved.
 */

/*
 * Copyright (c) 2009 Mangala Solutions Ltd, all rights reserved.
 */

package cazcade.cli.commands;

import cazcade.common.Logger;

/**
 * @author Neil Ellis
 */

public abstract class AbstractRecurringCommand extends AbstractCommand implements Command {
    private final static Logger log = Logger.getLogger(AbstractRecurringCommand.class);

    public boolean isShortLived() {
        return false;
    }
}