/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl;

import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class SortUtil {
    public static void sort(final List<Entity> entities, @Nullable final ChildSortOrder order) {
        if (order == ChildSortOrder.AGE || order == null) {
            dateSortEntities(entities);
        } else if (order == ChildSortOrder.POPULARITY) {
            popularitySortEntities(entities);
        } else if (order == ChildSortOrder.NONE) {
            return;
        } else {
            throw new UnsupportedOperationException("Order " + order + " not supported yet.");
        }
    }

    public static <T extends Entity> void dateSortEntities(final List<T> entities) {
        Collections.sort(entities, new Comparator<T>() {
            public int compare(@Nonnull final T lsdEntity, @Nonnull final T lsdEntity1) {
                if (lsdEntity.has$(Dictionary.UPDATED) && lsdEntity1.has$(Dictionary.UPDATED)) {
                    final Long updated = Long.valueOf(lsdEntity.$(Dictionary.UPDATED));
                    final Long updated1 = Long.valueOf(lsdEntity1.$(Dictionary.UPDATED));
                    return -updated.compareTo(updated1);
                } else {
                    return 0;
                }
            }
        });
    }

    public static void popularitySortEntities(final List<Entity> entities) {
        Collections.sort(entities, new Comparator<Entity>() {
            public int compare(@Nonnull final Entity lsdEntity, @Nonnull final Entity lsdEntity1) {
                if (lsdEntity.has$(Dictionary.POPULARITY_METRIC) && lsdEntity1.has$(Dictionary.POPULARITY_METRIC)) {
                    final Long popularity = Long.valueOf(lsdEntity.$(Dictionary.POPULARITY_METRIC));
                    final Long popularity1 = Long.valueOf(lsdEntity1.$(Dictionary.POPULARITY_METRIC));
                    return -popularity.compareTo(popularity1);
                } else {
                    return 0;
                }
            }
        });
    }
}
