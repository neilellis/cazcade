package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.widgets.client.form.fields.VortexFormField;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author neilellis@cazcade.com
 */
public abstract class EntityBackedFormPanel extends EntityBackedPanel {

    public void addBinding(LSDEntity otherEntity, final Bindable field, final LSDAttribute attribute) {
        field.bind(otherEntity, attribute, getReferenceDataPrefix());
        final Runnable onEnterAction = getUpdateEntityAction(field);
        field.setOnChangeAction(onEnterAction);
    }

    public void addBinding(final Bindable field, final LSDAttribute attribute) {
        field.bind(entity, attribute, getReferenceDataPrefix());
        final Runnable onEnterAction = getUpdateEntityAction(field);
        field.setOnChangeAction(onEnterAction);
    }


    @Override
    protected void bind(LSDEntity entity) {
        if(entity == null) {
            throw new NullPointerException("Attempted to bind to a null entity.");
        }
        setEntityInternal(entity);
    }

    protected abstract String getReferenceDataPrefix();

    protected abstract Runnable getUpdateEntityAction(Bindable field);



}