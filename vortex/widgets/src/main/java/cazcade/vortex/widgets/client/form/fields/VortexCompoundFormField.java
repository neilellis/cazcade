/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class VortexCompoundFormField extends AbstractVortexFormField {
    @Override
    public boolean isValid() {
        return true;
    }

    public void bindAll(@Nonnull final LSDBaseEntity parentEntity, @Nonnull final Map<LSDAttribute, VortexFormField> fields, final String prefix, final LSDAttribute parentAttribute) {
        for (final Map.Entry<LSDAttribute, VortexFormField> entry : fields.entrySet()) {
            entry.getValue().bind(getEntity(), entry.getKey(), prefix);
            entry.getValue().addChangeHandler(new ValueChangeHandler() {
                @Override public void onValueChange(ValueChangeEvent event) {
                    parentEntity.removeSubEntity(parentAttribute);
                    parentEntity.addAnonymousSubEntity(parentAttribute, getEntity());
                    ValueChangeEvent.fire(VortexCompoundFormField.this, entry.getValue().getStringValue());
                }
            });
        }
    }

}
