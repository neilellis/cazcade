package cazcade.liquid.impl;

import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class SortUtil {
    public static void dateSortEntities(List<LSDEntity> entities) {
        Collections.sort(entities, new Comparator<LSDEntity>() {
            public int compare(LSDEntity lsdEntity, LSDEntity lsdEntity1) {
                if (lsdEntity.hasAttribute(LSDAttribute.UPDATED) && lsdEntity1.hasAttribute(LSDAttribute.UPDATED)) {
                    final Long updated = Long.valueOf(lsdEntity.getAttribute(LSDAttribute.UPDATED));
                    final Long updated1 = Long.valueOf(lsdEntity1.getAttribute(LSDAttribute.UPDATED));
                    return -updated.compareTo(updated1);
                } else {
                    return 0;
                }
            }
        });
    }

    public static void popularitySortEntities(List<LSDEntity> entities) {
        Collections.sort(entities, new Comparator<LSDEntity>() {
            public int compare(LSDEntity lsdEntity, LSDEntity lsdEntity1) {
                if (lsdEntity.hasAttribute(LSDAttribute.POPULARITY_METRIC) && lsdEntity1.hasAttribute(LSDAttribute.POPULARITY_METRIC)) {
                    final Long popularity = Long.valueOf(lsdEntity.getAttribute(LSDAttribute.POPULARITY_METRIC));
                    final Long popularity1 = Long.valueOf(lsdEntity1.getAttribute(LSDAttribute.POPULARITY_METRIC));
                    return -popularity.compareTo(popularity1);
                } else {
                    return 0;
                }
            }
        });
    }

    public static void sort(List<LSDEntity> entities, ChildSortOrder order) {
        if (order == ChildSortOrder.AGE || order == null) {
            dateSortEntities(entities);
        } else if (order == ChildSortOrder.POPULARITY) {
            popularitySortEntities(entities);
        } else {
            throw new UnsupportedOperationException("Order " + order + " not supported yet.");
        }
    }
}
