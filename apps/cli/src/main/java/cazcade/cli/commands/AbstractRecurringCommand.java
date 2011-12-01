/*
 * Copyright (c) 2009 Mangala Solutions Ltd, all rights reserved.
 */

/*
 * Copyright (c) 2009 Mangala Solutions Ltd, all rights reserved.
 */

package cazcade.cli.commands;

import cazcade.common.Logger;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public abstract class AbstractRecurringCommand extends AbstractCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(AbstractRecurringCommand.class);

    public boolean isShortLived() {
        return false;
    }
}