package cazcade.vortex.pool.api;

import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.pool.PoolMode;
import cazcade.vortex.pool.objects.PoolObjectPresenter;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * @author neilellis@cazcade.com
 */
public interface PoolPresenter {

    PoolMode getMode();

    int getWidth();

    int getHeight();

    void captureUIEvents();

    void releaseUIEvents();

    boolean isWithinXBounds(int x);

    boolean isWithinYBounds(int i);


    LSDEntity getEntity();

    void move(PoolObjectPresenter presenter, double x, double y, boolean onServer);

    int getAbsoluteLeft();

    int getAbsoluteTop();

    AbsolutePanel getDragBoundContainer();

    void add(PoolObjectPresenter poolObjectPresenter);

    int getOffsetX();

    int getOffsetY();

    void transfer(PoolObjectPresenter source, PoolObjectPresenterContainer destination);

    LSDDictionaryTypes getType();

    void destroy();

    boolean isPageFlow();

    void showDragMode();

    void hideDragMode();

    void showInitMode();

    void hideInitMode();
}
