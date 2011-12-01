package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.lsd.LSDEntity;

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

    public void addChangedFollowedBoard(@Nonnull LSDEntity boardEntity) {
        changedFollowedBoards.add(boardEntity.getCamelCaseMap());
    }

    public void addChangedOwnedBoard(@Nonnull LSDEntity boardEntity) {
        changedOwnedBoards.add(boardEntity.getCamelCaseMap());
    }


    @Nonnull
    public List<Map> getChangedFollowedBoards() {
        if (changedFollowedBoards.size() > 5) {
            return changedFollowedBoards.subList(0, 5);
        } else {
            return changedFollowedBoards;
        }
    }

    @Nonnull
    public List<Map> getChangedOwnedBoards() {
        if (changedOwnedBoards.size() > 5) {
            return changedOwnedBoards.subList(0, 5);
        } else {
            return changedOwnedBoards;
        }
    }

    public boolean hasChangedFollowedBoards() {
        return changedFollowedBoards.size() > 0;
    }

    public boolean hasChangedOwnedBoards() {
        return changedOwnedBoards.size() > 0;
    }

    public void setLatestChanges(@Nonnull Collection<LSDEntity> changes) {
        for (LSDEntity change : changes) {
            latestChanges.add(change.getCamelCaseMap());
        }
    }

    @Nonnull
    public List<Map> getLatestChanges() {
        return latestChanges;
    }

    public boolean hasLatestChanges() {
        return latestChanges.size() > 0;
    }
}
