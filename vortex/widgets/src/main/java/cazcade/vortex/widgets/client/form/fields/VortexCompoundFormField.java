package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

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

    public void bindAll(@Nonnull final LSDEntity parentEntity, @Nonnull Map<LSDAttribute, VortexFormField> fields, String prefix, final LSDAttribute parentAttribute) {
        for (final Map.Entry<LSDAttribute, VortexFormField> entry : fields.entrySet()) {
            entry.getValue().bind(this.getEntity(), entry.getKey(), prefix);
            entry.getValue().setOnChangeAction(new Runnable() {
                @Override
                public void run() {
                    parentEntity.removeSubEntity(parentAttribute);
                    parentEntity.addAnonymousSubEntity(parentAttribute, VortexCompoundFormField.this.getEntity());
                    onChangeAction.run();
                }
            });
        }
    }

}
