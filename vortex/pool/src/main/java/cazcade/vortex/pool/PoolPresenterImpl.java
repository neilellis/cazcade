package cazcade.vortex.pool;

import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.MovePoolObjectRequest;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.pool.api.PoolObjectContainer;
import cazcade.vortex.pool.api.PoolObjectPresenterContainer;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.PoolObjectPresenter;
import cazcade.vortex.widgets.client.panels.scroll.VortexScrollPanel;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author neilellis@cazcade.com
 */
public class PoolPresenterImpl implements PoolPresenter, PoolObjectContainer {

    public static final int BORDER_BEFORE_PAGEFLOW_STARTS = 0;
    public static final int DEFAULT_HEIGHT = 636;
    private VortexScrollPanel scrollPanel;
    private AbsolutePanel panel;
    private LSDEntity entity;
    private boolean pageFlow;
    private VortexThreadSafeExecutor threadSafeExecutor;
    private int width = 1024;
    private int height = DEFAULT_HEIGHT;

    private final PoolObjectContainerManager poolObjectContainerManager;

    public PoolPresenterImpl(VortexScrollPanel scrollPanel, AbsolutePanel panel, final LSDEntity entity, final FormatUtil features, final VortexThreadSafeExecutor threadSafeExecutor) {

        this(scrollPanel, panel, entity, false, features, threadSafeExecutor);
    }

    public PoolPresenterImpl(VortexScrollPanel scrollPanel, final AbsolutePanel panel, final LSDEntity entity, boolean pageFlow, final FormatUtil features, final VortexThreadSafeExecutor threadSafeExecutor) {
        this.scrollPanel = scrollPanel;
        this.panel = panel;
        this.entity = entity;
        this.pageFlow = pageFlow;
        if (pageFlow) {
            panel.setHeight(height + "px");
            scrollPanel.setHeight(height + "px");
        }
        this.threadSafeExecutor = threadSafeExecutor;
        poolObjectContainerManager = new PoolObjectContainerManager(this, threadSafeExecutor, entity.getURI(), features);
        if (pageFlow) {
            new Timer() {
                @Override
                public void run() {
                    final int widgetCount = panel.getWidgetCount();
                    int minHeight = DEFAULT_HEIGHT;
                    for (int i = 0; i < widgetCount; i++) {
                        final Widget widget = panel.getWidget(i);
                        final int maxY = widget.getElement().getOffsetTop() + widget.getOffsetHeight();
                        if (maxY > minHeight) {
                            minHeight = maxY + BORDER_BEFORE_PAGEFLOW_STARTS;
                        }
                    }
                    height = minHeight;
                    adjustHeight();
                }
            }.scheduleRepeating(1000);
        }

    }

    public PoolMode getMode() {
        return null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void captureUIEvents() {
        DOM.setCapture(panel.getElement());
    }

    public void releaseUIEvents() {
        DOM.releaseCapture(panel.getElement());
    }

    public boolean isWithinXBounds(int x) {
        return false; //TODO
    }

    public boolean isWithinYBounds(int i) {
        return false; //TODO
    }

    public AbsolutePanel getPanel() {
        return panel;
    }

    public LSDEntity getEntity() {
        return entity;
    }

    @Override
    public void move(PoolObjectPresenter presenter, final double x, final double y, boolean onServer) {
        if (onServer) {
            BusFactory.getInstance().dispatch(new MovePoolObjectRequest(presenter.getEntity().getURI(), x, y, 0.0));
        } else {
            final Widget widget = presenter.getPoolObjectView();
            if (widget.getParent() != panel) {
                ClientLog.log("Widget parent was " + widget.getParent() + " not " + panel);
                ClientLog.log("Offending widget was " + widget);
                throw new IllegalArgumentException("Pool widget does not have this as a parent, check the log for more information.");
            } else {
                double newX = x;
                double newY = y;
                if (newX > width - widget.getOffsetWidth()) {
                    newX = width - widget.getOffsetWidth();
                }
                if (newX < 0) {
                    newX = 0;
                }
                if (newY < 0) {
                    newY = 0;
                }

                if (!pageFlow) {
                    if (newY > height - widget.getOffsetHeight()) {
                        newY = height - widget.getOffsetHeight();
                    }
                }
                panel.setWidgetPosition(widget, (int) newX, (int) newY);
                presenter.setX(newX);
                presenter.setY(newY);
                //No done in the timer thread

//                if (pageFlow) {
//                    final int newHeight = (int) newY + widget.getOffsetHeight() + 20;
//                    if (newHeight > (height - BORDER_BEFORE_PAGEFLOW_STARTS)) {
//                        height = newHeight + BORDER_BEFORE_PAGEFLOW_STARTS;
//                        adjustHeight();
//                    }
//                }
            }
            poolObjectContainerManager.checkForCollisions(presenter);
        }
    }

    private void adjustHeight() {
        scrollPanel.setHeight(height + "px");
        panel.setHeight(height + "px");
    }

    @Override
    public void moveToVisibleCentre(PoolObjectPresenter poolObjectPresenter) {
        move(poolObjectPresenter, scrollPanel.getOffsetX() + scrollPanel.getOffsetWidth() / 2, scrollPanel.getOffsetY() + scrollPanel.getOffsetHeight() / 2, true);
    }

    public int getAbsoluteLeft() {
        return panel.getAbsoluteLeft();
    }

    public int getAbsoluteTop() {
        return panel.getAbsoluteTop();
    }

    public AbsolutePanel getDragBoundContainer() {
        return panel;
    }

    public void add(final PoolObjectPresenter poolObjectPresenter) {
        poolObjectContainerManager.add(poolObjectPresenter, false);
    }

    public void remove(PoolObjectPresenter poolObjectPresenter) {
        poolObjectContainerManager.remove(poolObjectPresenter);
    }

    public int getOffsetX() {
        return scrollPanel.getOffsetX();
    }

    public int getOffsetY() {
        return scrollPanel.getOffsetY();
    }

    @Override
    public void transfer(PoolObjectPresenter source, PoolObjectPresenterContainer destination) {

        poolObjectContainerManager.transfer(source, destination);
    }

    @Override
    public LSDDictionaryTypes getType() {
        return LSDDictionaryTypes.POOL2D;
    }

    @Override
    public void destroy() {
        poolObjectContainerManager.destroy();
    }

    @Override
    public boolean isPageFlow() {
        return pageFlow;
    }

    @Override
    public void showDragMode() {
        panel.addStyleName("drag-mode");

    }

    @Override
    public void hideDragMode() {
        panel.removeStyleName("drag-mode");
    }


    public void showInitMode() {
        panel.addStyleName("init-mode");

    }

    public void hideInitMode() {
        panel.removeStyleName("init-mode");
    }


    public VortexThreadSafeExecutor getThreadSafeExecutor() {
        return threadSafeExecutor;
    }

    public void addView(Widget view) {
        WidgetUtil.addGracefully(panel, view);
    }

    public void removeView(Widget widget) {
        WidgetUtil.removeFromParentGracefully(widget);
    }
}
