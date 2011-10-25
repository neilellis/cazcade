package cazcade.vortex.pool.objects;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.MovePoolObjectRequest;
import cazcade.liquid.api.request.ResizePoolObjectRequest;
import cazcade.liquid.api.request.RotateXYPoolObjectRequest;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public interface PoolObjectPresenter<T extends PoolObjectView> {

    public LSDEntity getEntity();

    Widget getPoolObjectView();

    void select();

    /**
     * The pool presenter calls this method when the object is added to pool.
     */
    void onAddToPool();

    void onRemoveFromPool();

    void setOnDelete(Runnable runnable);

    double getX();

    double getY();

    double getLeft();

    double getRight();

    double getTop();

    double getBottom();

    void hide();

    void setX(double x);

    void setY(double y);
}
