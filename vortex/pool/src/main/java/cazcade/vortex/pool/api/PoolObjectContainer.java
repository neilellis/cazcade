package cazcade.vortex.pool.api;

import cazcade.vortex.pool.objects.PoolObjectPresenter;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public interface PoolObjectContainer extends PoolObjectPresenterContainer{

    void addView(Widget view);

    void removeView(Widget widget);

    void move(PoolObjectPresenter presenter, double x, double y, boolean onServer);

    void moveToVisibleCentre(PoolObjectPresenter poolObjectPresenter);
}
