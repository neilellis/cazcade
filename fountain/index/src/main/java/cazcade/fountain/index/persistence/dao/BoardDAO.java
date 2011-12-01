package cazcade.fountain.index.persistence.dao;

import cazcade.fountain.index.persistence.entities.BoardIndexEntity;
import cazcade.fountain.index.persistence.entities.VisitEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BoardDAO {

    @Transactional
    void saveBoard(BoardIndexEntity user);

    @Transactional
    BoardIndexEntity getOrCreateBoard(String uri);

    @Transactional
    List<BoardIndexEntity> getMyBoards(int from, int size, String aliasURI);

    @Transactional
    List<BoardIndexEntity> getUserBoards(int from, int size, String aliasURI);

    @Transactional
    List<BoardIndexEntity> getRecentBoards(int from, int size);

    @Transactional
    List<BoardIndexEntity> getVisitedBoards(int from, int size, String aliasURI);

    @Transactional
    List<BoardIndexEntity> getPopularBoards(int from, int size);


    @Transactional
    void addVisit(VisitEntity visitEntity);

    @Transactional
    String getUniqueVisitorCount(BoardIndexEntity board);
}
