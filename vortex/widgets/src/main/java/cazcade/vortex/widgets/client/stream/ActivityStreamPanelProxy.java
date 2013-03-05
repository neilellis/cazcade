/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.vortex.gwt.util.client.history.HistoryAware;
import com.google.gwt.user.client.AsyncProxy;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
@AsyncProxy.ConcreteType(ActivityStreamPanel.class) @AsyncProxy.AllowNonVoid
public interface ActivityStreamPanelProxy extends AsyncProxy<HistoryAware>, HistoryAware {}
