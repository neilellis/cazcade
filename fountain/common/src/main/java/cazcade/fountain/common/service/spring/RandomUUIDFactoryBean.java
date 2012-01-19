package cazcade.fountain.common.service.spring;

import org.springframework.beans.factory.FactoryBean;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Factory bean that supports the generation of random UUIDs.
 */
public class RandomUUIDFactoryBean implements FactoryBean {
    public Object getObject() throws Exception {
        return UUID.randomUUID();
    }

    @Nonnull
    public Class getObjectType() {
        return UUID.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
