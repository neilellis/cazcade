package cazcade.liquid.impl;

import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class SortUtil {
    public static void sort(final List<LSDBaseEntity> entities, @Nullable final ChildSortOrder order) {
        if (order == ChildSortOrder.AGE || order == null) {
            dateSortEntities(entities);
        }
        else if (order == ChildSortOrder.POPULARITY) {
            popularitySortEntities(entities);
        }
        else if (order == ChildSortOrder.NONE) {
            return;
        }
        else {
            throw new UnsupportedOperationException("Order " + order + " not supported yet.");
        }
    }

    public static <T extends LSDBaseEntity> void dateSortEntities(final List<T> entities) {
        Collections.sort(entities, new Comparator<T>() {
            public int compare(@Nonnull final T lsdEntity, @Nonnull final T lsdEntity1) {
                if (lsdEntity.hasAttribute(LSDAttribute.UPDATED) && lsdEntity1.hasAttribute(LSDAttribute.UPDATED)) {
                    final Long updated = Long.valueOf(lsdEntity.getAttribute(LSDAttribute.UPDATED));
                    final Long updated1 = Long.valueOf(lsdEntity1.getAttribute(LSDAttribute.UPDATED));
                    return -updated.compareTo(updated1);
                }
                else {
                    return 0;
                }
            }
        }
                        );
    }

    public static void popularitySortEntities(final List<LSDBaseEntity> entities) {
        Collections.sort(entities, new Comparator<LSDBaseEntity>() {
            public int compare(@Nonnull final LSDBaseEntity lsdEntity, @Nonnull final LSDBaseEntity lsdEntity1) {
                if (lsdEntity.hasAttribute(LSDAttribute.POPULARITY_METRIC) && lsdEntity1.hasAttribute(
                        LSDAttribute.POPULARITY_METRIC
                                                                                                     )) {
                    final Long popularity = Long.valueOf(lsdEntity.getAttribute(LSDAttribute.POPULARITY_METRIC));
                    final Long popularity1 = Long.valueOf(lsdEntity1.getAttribute(LSDAttribute.POPULARITY_METRIC));
                    return -popularity.compareTo(popularity1);
                }
                else {
                    return 0;
                }
            }
        }
                        );
    }
}
