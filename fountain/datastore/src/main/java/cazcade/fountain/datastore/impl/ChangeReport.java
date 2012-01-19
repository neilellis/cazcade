package cazcade.fountain.datastore.impl;

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
    @Nonnull
    private final List<Map> changedFollowedBoards = new ArrayList<Map>();
    @Nonnull
    private final List<Map> changedOwnedBoards = new ArrayList<Map>();
    @Nonnull
    private final List<Map> latestChanges = new ArrayList<Map>();

    public void addChangedFollowedBoard(@Nonnull final LSDTransferEntity boardEntity) {
        changedFollowedBoards.add(boardEntity.getCamelCaseMap());
    }

    public void addChangedOwnedBoard(@Nonnull final LSDTransferEntity boardEntity) {
        changedOwnedBoards.add(boardEntity.getCamelCaseMap());
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
            latestChanges.add(change.getCamelCaseMap());
        }
    }

    @Nonnull
    public List<Map> getLatestChanges() {
        return latestChanges;
    }
}
