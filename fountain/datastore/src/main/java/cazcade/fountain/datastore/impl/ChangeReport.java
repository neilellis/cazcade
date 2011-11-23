package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.lsd.LSDEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class ChangeReport {

    private List<Map> changedFollowedBoards = new ArrayList<Map>();
    private List<Map> changedOwnedBoards = new ArrayList<Map>();
    private List<Map> latestChanges = new ArrayList<Map>();

    public void addChangedFollowedBoard(LSDEntity boardEntity) {
        changedFollowedBoards.add(boardEntity.getCamelCaseMap());
    }

    public void addChangedOwnedBoard(LSDEntity boardEntity) {
        changedOwnedBoards.add(boardEntity.getCamelCaseMap());
    }


    public List<Map> getChangedFollowedBoards() {
        if (changedFollowedBoards.size() > 5) {
            return changedFollowedBoards.subList(0, 5);
        } else {
            return changedFollowedBoards;
        }
    }

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

    public void setLatestChanges(Collection<LSDEntity> changes) {
        for (LSDEntity change : changes) {
            latestChanges.add(change.getCamelCaseMap());
        }
    }

    public List<Map> getLatestChanges() {
        return latestChanges;
    }

    public boolean hasLatestChanges() {
        return latestChanges.size() > 0;
    }
}
