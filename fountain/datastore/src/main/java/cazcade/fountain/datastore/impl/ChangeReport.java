/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class ChangeReport {

    public static final int FORCE_IMAGE_REFRESH_TIME_IN_MILLIS = (1000 * 36000 * 24);

    @Nonnull
    private final List<Map> changedFollowedBoards = new ArrayList<Map>();
    @Nonnull
    private final List<Map> changedOwnedBoards    = new ArrayList<Map>();
    @Nonnull
    private final List<Map> latestChanges         = new ArrayList<Map>();

    public void addChangedFollowedBoard(@Nonnull final LSDTransferEntity boardEntity) {
        final Map<String, String> map = boardEntity.getCamelCaseMap();
        map.put("snapshotUrl", addBoardSnaphotImageUrl(boardEntity));
        changedFollowedBoards.add(map);
    }

    private static String addBoardSnaphotImageUrl(LSDTransferEntity boardEntity) {
        return "http://boardcast.it/_snapshot-" + boardEntity.getURI().asShortUrl().asUrlSafe() +
               "?id=" + boardEntity.getUUID().toString() + boardEntity.getAttribute(LSDAttribute.VERSION, "") +
               System.currentTimeMillis() / FORCE_IMAGE_REFRESH_TIME_IN_MILLIS;
    }

    public void addChangedOwnedBoard(@Nonnull final LSDTransferEntity boardEntity) {
        final Map<String, String> map = boardEntity.getCamelCaseMap();
        map.put("snapshotUrl", addBoardSnaphotImageUrl(boardEntity));
        changedOwnedBoards.add(map);
    }

    @Nonnull
    public List<Map> getChangedFollowedBoards() {
        if (changedFollowedBoards.size() > 5) {
            return changedFollowedBoards.subList(0, 5);
        }
        else {
            return changedFollowedBoards;
        }
    }

    @Nonnull
    public List<Map> getChangedOwnedBoards() {
        if (changedOwnedBoards.size() > 5) {
            return changedOwnedBoards.subList(0, 5);
        }
        else {
            return changedOwnedBoards;
        }
    }

    public boolean hasChangedFollowedBoards() {
        return !changedFollowedBoards.isEmpty();
    }

    public boolean hasChangedOwnedBoards() {
        return !changedOwnedBoards.isEmpty();
    }

    public boolean hasLatestChanges() {
        return !latestChanges.isEmpty();
    }

    public void setLatestChanges(@Nonnull final Collection<LSDTransferEntity> changes) {
        for (final LSDTransferEntity change : changes) {
            final Map<String, String> map = change.getCamelCaseMap();
            //            map.put("snapshotUrl", addBoardSnaphotImageUrl(change));
            latestChanges.add(map);
        }
    }

    @Nonnull
    public List<Map> getLatestChanges() {
        return latestChanges;
    }
}
