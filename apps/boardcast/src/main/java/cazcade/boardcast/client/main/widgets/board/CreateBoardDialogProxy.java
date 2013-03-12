/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.board;

import cazcade.vortex.gwt.util.client.history.HistoryAware;
import com.google.gwt.user.client.AsyncProxy;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
@AsyncProxy.ConcreteType(CreateBoardDialog.class) @AsyncProxy.AllowNonVoid
public interface CreateBoardDialogProxy extends AsyncProxy<HistoryAware>, HistoryAware {}
