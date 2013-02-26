/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.Permission;
import cazcade.liquid.api.PermissionScope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class TransferEntityCollection<T extends TransferEntity<T>> extends AbstractList<T> {

    private final List<T> values;

    public TransferEntityCollection(List<T> values) {
        this.values = values;
    }

    public TransferEntityCollection(final Iterable<T> values) {
        this.values = new ArrayList<T>();
        for (final T value : values) {
            this.values.add(value);
        }
    }

    public TransferEntityCollection() {
        this.values = new ArrayList<T>();
    }

    @Override public T get(int index) {
        return values.get(index);
    }

    public TransferEntityCollection<T> each(CollectionCallback<T> callback) {
        for (final T value : values) {
            callback.call(value);
        }
        return this;

    }

    @Nonnull @Override public Iterator<T> iterator() {
        return values.iterator();
    }

    @Override public int size() {
        return values.size();
    }

    private <V> ObjectCollection<V> toObjectCollection(EntityIterationCallback<T, V> callback) {
        return new ObjectCollection<V>(toList(callback));
    }

    private <V> ArrayList<V> toList(EntityIterationCallback<T, V> callback) {
        final ArrayList<V> results = new ArrayList<V>();
        for (final T value : values) {
            final V result = callback.call(value);
            if (result != null) {
                results.add(result);
            }
        }
        return results;
    }

    private <U extends TransferEntity<U>> TransferEntityCollection<U> toAll(final EntityIterationCallback<T, U> callback) {
        final ArrayList<U> results = new ArrayList<U>();
        for (final T value : values) {
            final U result = callback.call(value);
            if (result != null) {
                results.add(result);
            }
        }
        return new TransferEntityCollection<U>(results);
    }

    private boolean isAll(final EntityIterationCallback<T, Boolean> callback) {
        for (final T value : values) {
            final Boolean result = callback.call(value);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    private boolean isAny(final EntityIterationCallback<T, Boolean> callback) {
        for (final T value : values) {
            final Boolean result = callback.call(value);
            if (result) {
                return true;
            }
        }
        return false;
    }

    private <U> U throwUnsupported() {
        throw new UnsupportedOperationException("This operation is not valid for an entity collection");
    }

    @Nonnull public TransferEntityCollection asUpdateEntity() {
        return toAll(new EntityIterationCallback() {
            public Object call(final TransferEntity entity) {
                return ((TransferEntity) entity).asUpdateEntity();
            }
        });
    }

    @Nonnull public TransferEntityCollection<T> $() {
        return toAll(new EntityIterationCallback() {
            public Object call(TransferEntity entity) {
                return ((TransferEntity) entity).$();
            }
        });
    }

    public String dump() {
        final StringBuffer buffer = new StringBuffer();
        toAll(new EntityIterationCallback() {
            @Override public Object call(TransferEntity entity) {
                buffer.append(((TransferEntity) entity).dump());
                return entity;
            }
        });
        return buffer.toString();
    }

    public void addAnonymousSubEntity(@Nonnull final Attribute stem, @Nonnull final Entity anon) {
        toAll(new EntityIterationCallback<T, T>() {
            public T call(T entity) {
                entity.addAnonymousSubEntity(stem, anon);
                return null;
            }
        });
    }

    public void children(@Nonnull final Attribute stem, @Nonnull final Collection<? extends T> collection) {
        toAll(new EntityIterationCallback<T, T>() {
            public T call(T entity) {
                entity.children(stem, collection);
                return null;
            }
        });
    }

    public void child(@Nonnull final Attribute stem, @Nonnull final T child, final boolean requiresId) {
        toAll(new EntityIterationCallback<T, T>() {
            public T call(T entity) {
                entity.child(stem, child, requiresId);
                return null;
            }
        });
    }

    @Nonnull public String asFreeText() {
        final StringBuffer buffer = new StringBuffer();
        toAll(new EntityIterationCallback() {
            @Override public Object call(TransferEntity entity) {
                buffer.append(((TransferEntity) entity).asFreeText());
                return entity;
            }
        });
        return buffer.toString();

    }

    @Nonnull public String asDebugText() {
        final StringBuffer buffer = new StringBuffer();
        toAll(new EntityIterationCallback() {
            @Override public Object call(TransferEntity entity) {
                buffer.append(((TransferEntity) entity).asDebugText());
                return entity;
            }
        });
        return buffer.toString();

    }

    public boolean attributeIs(@Nonnull final Attribute attribute, @Nonnull final String comparison) {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            public Boolean call(T entity) {
                return entity.attributeIs(attribute, comparison);
            }
        });
    }

    public boolean canBe(@Nonnull final Types type) {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            public Boolean call(T entity) {
                return entity.canBe(type);
            }
        });
    }

    public TransferEntityCollection<T> $(@Nonnull final Entity other, @Nonnull final Attribute attribute) {
        return toAll(new EntityIterationCallback<T, T>() {
            public T call(T entity) {
                return entity.$(other, attribute);
            }
        });
    }

    @Nonnull public ObjectCollection<String> default$(@Nonnull final Attribute attribute, @Nonnull final String defaultValue) {
        return toObjectCollection(new EntityIterationCallback<T, String>() {
            @Override public String call(T entity) {
                return entity.default$(attribute, defaultValue);
            }
        });
    }

    @Nonnull public ObjectCollection<String> $(@Nonnull final Attribute attribute) {
        return toObjectCollection(new EntityIterationCallback<T, String>() {
            @Override public String call(T entity) {
                return entity.$(attribute);
            }
        });
    }

    @Nonnull public ObjectCollection<List<String>> $list(@Nonnull final Attribute attribute) {
        return toObjectCollection(new EntityIterationCallback<T, List<String>>() {
            @Override public List<String> call(T entity) {
                return entity.$list(attribute);
            }
        });
    }

    @Nonnull public ObjectCollection<LiquidURI> $uri(@Nonnull final Attribute attribute) {
        return toObjectCollection(new EntityIterationCallback<T, LiquidURI>() {
            @Override public LiquidURI call(T entity) {
                return entity.$uri(attribute);
            }
        });
    }

    public ObjectCollection<Boolean> default$bool(@Nonnull final Attribute attribute, final boolean defaultValue) {
        return toObjectCollection(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.default$bool(attribute, defaultValue);
            }
        });
    }

    public ObjectCollection<Boolean> $bool(@Nonnull final Attribute attribute) {
        return toObjectCollection(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.$bool(attribute);
            }
        });
    }

    @Nonnull public ObjectCollection<Double> $d(@Nonnull final Attribute attribute) throws NumberFormatException {
        return toObjectCollection(new EntityIterationCallback<T, Double>() {
            @Override public Double call(T entity) {
                return entity.$d(attribute);
            }
        });
    }

    public ObjectCollection<Integer> default$i(@Nonnull final Attribute attribute, final int defaultValue) throws NumberFormatException {
        return toObjectCollection(new EntityIterationCallback<T, Integer>() {
            @Override public Integer call(T entity) {
                return entity.default$i(attribute, defaultValue);
            }
        });
    }

    @Nonnull public ObjectCollection<Integer> $i(@Nonnull final Attribute attribute) throws NumberFormatException {
        return toObjectCollection(new EntityIterationCallback<T, Integer>() {
            @Override public Integer call(T entity) {
                return entity.$i(attribute);
            }
        });
    }

    @Nonnull public ObjectCollection<Long> $l(@Nonnull final Attribute attribute) throws NumberFormatException {
        return toObjectCollection(new EntityIterationCallback<T, Long>() {
            @Override public Long call(T entity) {
                return entity.$l(attribute);
            }
        });
    }

    @Nonnull public ObjectCollection<Map<String, String>> map() {
        return toObjectCollection(new EntityIterationCallback<T, Map<String, String>>() {
            @Override public Map<String, String> call(T entity) {
                return entity.map();
            }
        });
    }

    @Nonnull public ObjectCollection<Date> published() {
        return toObjectCollection(new EntityIterationCallback<T, Date>() {
            @Override public Date call(T entity) {
                return entity.published();
            }
        });
    }

    public TransferEntityCollection<T> published(final Date published) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                entity.published(published);
                return entity;
            }
        });
    }

    @Nullable
    public ObjectCollection<String> default$sub(@Nonnull final Attribute attribute, @Nonnull final Attribute subAttribute, final String defaultValue) {
        return toObjectCollection(new EntityIterationCallback<T, String>() {
            @Override public String call(T entity) {
                return entity.default$sub(attribute, subAttribute, defaultValue);
            }
        });
    }

    @Nonnull public TransferEntityCollection<T> children(@Nonnull final Attribute key) {
        final TransferEntityCollection<T> entities = new TransferEntityCollection<T>();
        toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                entities.add(entity.children(key));
                return entity;
            }
        });
        return entities;
    }

    private TransferEntityCollection<T> add(Iterable<? extends T> entities) {
        for (T entity : entities) {
            this.values.add(entity);
        }
        return this;
    }


    private Iterable<T> asIterable() {
        return values;
    }

    private Collection<T> asCollection() {
        return values;
    }

    @Nonnull
    public <U extends TransferEntity<U>> TransferEntityCollection<U> child(@Nonnull final Attribute path, final boolean readonly) {
        return toAll(new EntityIterationCallback<T, U>() {
            @Override public U call(T entity) {
                return (U) entity.child(path, readonly);
            }
        });
    }

    @Nonnull public ObjectCollection<TypeDef> type() {
        return toObjectCollection(new EntityIterationCallback<T, TypeDef>() {
            @Override public TypeDef call(T entity) {
                return entity.type();
            }
        });
    }

    @Nonnull public ObjectCollection<LiquidURI> uri() {
        return toObjectCollection(new EntityIterationCallback<T, LiquidURI>() {
            @Override public LiquidURI call(T entity) {
                return entity.uri();
            }
        });
    }

    public TransferEntityCollection<T> uri(final LiquidURI uri) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                entity.uri(uri);
                return entity;
            }
        });
    }

    public boolean hasURI() {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.hasURI();
            }
        });
    }

    @Nonnull public ObjectCollection<LiquidUUID> id() {
        return toObjectCollection(new EntityIterationCallback<T, LiquidUUID>() {
            @Override public LiquidUUID call(T entity) {
                return entity.id();
            }
        });
    }

    @Nonnull public ObjectCollection<LiquidURI> $uuid(@Nonnull final Attribute attribute) {
        return toObjectCollection(new EntityIterationCallback<T, LiquidURI>() {
            @Override public LiquidURI call(T entity) {
                return entity.getURIAttribute(attribute);
            }
        });
    }

    @Nonnull public ObjectCollection<Date> updated() {
        return toObjectCollection(new EntityIterationCallback<T, Date>() {
            @Override public Date call(T entity) {
                return entity.updated();
            }
        });
    }

    public TransferEntityCollection<T> updated(final Date updated) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                entity.updated(updated);
                return entity;
            }
        });
    }

    public boolean has$(@Nonnull final Attribute key) {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.has$(key);
            }
        });
    }

    public boolean allowed(@Nonnull final PermissionScope permissionScope, @Nonnull final Permission permission) {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.allowed(permissionScope, permission);
            }
        });
    }

    public boolean hasChild(@Nonnull final Attribute attribute) {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.hasChild(attribute);
            }
        });
    }

    public boolean is(@Nonnull final Types type) {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.is(type);
            }
        });
    }

    public boolean is(@Nonnull final TypeDef typeDef) {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.is(typeDef);
            }
        });
    }

    public boolean isEmptyValue(@Nonnull final Attribute key) {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.isEmptyValue(key);
            }
        });
    }

    public boolean error() {
        return isAny(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.error();
            }
        });
    }

    public boolean isNewerThan(final Entity other) {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.isNewerThan(other);
            }
        });
    }

    public ObjectCollection<Boolean> readonly() {
        return toObjectCollection(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.readonly();
            }
        });
    }

    public TransferEntityCollection<T> readonly(final boolean readonly) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                entity.readonly(readonly);
                return entity;
            }
        });
    }

    public TransferEntityCollection<T> remove(@Nonnull final Attribute attribute) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                entity.remove(attribute);
                return entity;
            }
        });
    }

    public TransferEntityCollection<T> removeCompletely(@Nonnull final Attribute attribute) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                entity.removeCompletely(attribute);
                return entity;
            }
        });
    }

    @Nonnull public <U extends TransferEntity<U>> TransferEntityCollection<U> removeChild(@Nonnull final Attribute path) {
        return toAll(new EntityIterationCallback<T, U>() {
            @Override public U call(T entity) {
                return (U) entity.removeChild(path);
            }
        });
    }

    public TransferEntityCollection<T> remove$(@Nonnull final Attribute attribute) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                entity.removeCompletely(attribute);
                return entity;
            }
        });
    }

    public TransferEntityCollection<T> $(@Nonnull final Attribute parent, @Nonnull final Attribute child, final String value) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                entity.$(parent, child, value);
                return entity;
            }
        });
    }

    public TransferEntityCollection<T> $(@Nonnull final Attribute key, final String value) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.$(key, value);
            }
        });
    }

    public TransferEntityCollection<T> $(@Nonnull final Attribute attribute, final boolean bool) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.$(attribute, bool);
            }
        });
    }

    public TransferEntityCollection<T> $(@Nonnull final Attribute attribute, final long value) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.$(attribute, value);
            }
        });
    }

    public TransferEntityCollection<T> $(@Nonnull final Attribute attribute, final LiquidUUID uuid) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.$(attribute, uuid);
            }
        });
    }

    public TransferEntityCollection<T> $(@Nonnull final Attribute attribute, final LiquidURI uri) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.$(attribute, uri);
            }
        });
    }

    public TransferEntityCollection<T> $(@Nonnull final Attribute attribute, final double value) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.$(attribute, value);
            }
        });
    }

    public TransferEntityCollection<T> $(@Nonnull final Attribute attribute, final Date value) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.$(attribute, value);
            }
        });
    }

    public TransferEntityCollection<T> $notnull(@Nonnull final Attribute attribute, final String value) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.$(attribute, value);
            }
        });
    }

    public TransferEntityCollection<T> id(@Nonnull final LiquidUUID id) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.id(id);
            }
        });
    }

    public TransferEntityCollection<T> id(final String id) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.id(id);
            }
        });
    }

    public TransferEntityCollection<T> $(@Nonnull final Attribute attribute, final List values) {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.$(attribute, values);
            }
        });
    }

    public TransferEntityCollection<T> timestamp() {
        return toAll(new EntityIterationCallback<T, T>() {
            @Override public T call(T entity) {
                return entity.timestamp();
            }
        });
    }

    public boolean wasPublishedAfter(@Nonnull final Entity other) {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.wasPublishedAfter(other);
            }
        });
    }

    public boolean hasUpdated() {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.hasUpdated();
            }
        });
    }

    public boolean hasId() {
        return isAll(new EntityIterationCallback<T, Boolean>() {
            @Override public Boolean call(T entity) {
                return entity.hasId();
            }
        });
    }

    public ObjectCollection<String> nameOrId() {
        return toObjectCollection(new EntityIterationCallback<T, String>() {
            @Override public String call(T entity) {
                return entity.nameOrId();
            }
        });
    }

    public <U extends TransferEntity<U>> TransferEntityCollection<U> merge(final T otherEntity, final boolean destructive) {
        return toAll(new EntityIterationCallback<T, U>() {
            @Override public U call(T entity) {
                return (U) entity.merge(otherEntity, destructive);
            }
        });
    }

    public ObjectCollection<String> $sub(@Nonnull final Attribute attribute, @Nonnull final Attribute subAttribute) {
        return toObjectCollection(new EntityIterationCallback<T, String>() {
            @Override public String call(T entity) {
                return entity.$sub(attribute, subAttribute);
            }
        });
    }

    public TransferEntityCollection<T> filter(CollectionPredicate<T> collectionPredicate) {
        List<T> result = new ArrayList<T>();
        for (T value : values) {
            if (collectionPredicate.call(value)) {
                result.add(value);
            }
        }
        return new TransferEntityCollection<T>(result);
    }
}
