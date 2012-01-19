package cazcade.fountain.index.persistence.dao;


import cazcade.fountain.index.model.BoardType;
import cazcade.fountain.index.persistence.entities.BoardIndexEntity;
import cazcade.fountain.index.persistence.entities.VisitEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class BoardDAOImpl implements BoardDAO {
    public static final SimpleExpression LISTED = Restrictions.eq("listed", true);
    public static final SimpleExpression PUBLIC_BOARD = Restrictions.eq("type", BoardType.PUBLIC);

    private final Logger log = LoggerFactory.getLogger(BoardDAOImpl.class);
    private HibernateTemplate hibernateTemplate;

    private SessionFactory sessionFactory;

    public static Pattern userNamePattern() {
        return Pattern.compile("@[a-zA-Z_0-9]+");
    }

    @Override
    public void addVisit(final VisitEntity visitEntity) {
        hibernateTemplate.persist(visitEntity);
    }

    @Override
    public List<BoardIndexEntity> getMyBoards(final int from, final int size, final String aliasURI) {
        return sessionFactory.getCurrentSession()
                             .createQuery(
                                     "from BoardIndexEntity b where b.owner.uri = :alias  or b.author.uri= :alias or b.creator.uri=:alias order by b.updated desc"
                                         )
                             .setParameter("alias", aliasURI)
                             .setFirstResult(from)
                             .setMaxResults(size
                                           )
                             .list();
    }

    @Override
    public BoardIndexEntity getOrCreateBoard(final String uri) {
        return hibernateTemplate.execute(new HibernateCallback<BoardIndexEntity>() {
            @Nonnull
            @Override
            public BoardIndexEntity doInHibernate(@Nonnull final Session session) throws HibernateException, SQLException {
                final List boards = hibernateTemplate.find("from BoardIndexEntity where uri=?", uri);
                if (boards.isEmpty()) {
                    final BoardIndexEntity board = new BoardIndexEntity();
                    board.setUri(uri);
                    board.setUpdated(new Date());
                    board.setCreated(new Date());
                    session.persist(board);
                    return board;
                }
                else {
                    final BoardIndexEntity board = (BoardIndexEntity) boards.get(0);
                    return board;
                }
            }
        }
                                        );
    }

    //    @Override
    public List<BoardIndexEntity> getPopularBoards(final int from, final int size) {
        return sessionFactory.getCurrentSession()
                             .createCriteria(BoardIndexEntity.class)
                             .add(PUBLIC_BOARD)
                             .add(LISTED)
                             .addOrder(Order.desc("popularity")
                                      )
                             .setFirstResult(from)
                             .setMaxResults(size)
                             .list();
    }

    /*
    public List<MessageEntity> getLastEntries(final String boardName, final int max, final String sinceEntryId, final AliasEntity currentAlias) {
        return hibernateTemplate.execute(new HibernateCallback<List<MessageEntity>>() {
            @Override
            public List<MessageEntity> doInHibernate(Session session) throws HibernateException, SQLException {
                hibernateTemplate.setMaxResults(max);
                List result;
                Date fromDate = new Date(0);
                if (sinceEntryId != null) {
                    fromDate = (Date) session.createQuery("select created from MessageEntity s where s.id= ?").setParameter(0, sinceEntryId).uniqueResult();
                }
                if (BoardUtil.isUserName(boardName)) {
                    if (sinceEntryId == null) {
                        result = hibernateTemplate.find("from MessageEntity e where e.board.accessModel in (?,?) and e.author.name = ? order by e.created desc", BoardType.PUBLIC, BoardType.PERSONAL, boardName.substring(1));
                    } else {
                        result = hibernateTemplate.find("from MessageEntity e where e.board.accessModel in (?,?) and e.author.name = ? and e.created > ? order by e.created desc", BoardType.PUBLIC, BoardType.PERSONAL, boardName.substring(1), fromDate);
                    }
                } else if (BoardUtil.isQueryBoardName(boardName)) {
                    if (boardName.equals("!timeline")) {
                        result = hibernateTemplate.find("from MessageEntity e where e.board.accessModel=? and created > ? order by e.created desc", BoardType.PUBLIC, fromDate);
                    } else if (boardName.equals("!related")) {
                        result = hibernateTemplate.find("from MessageEntity e where e.board.name in (select e.board.name from MessageEntity e where e.author = ?) and created > ?  order by e.created desc", currentAlias, fromDate);
                    } else {
                        result = new ArrayList();
                    }
                } else {
                    if (BoardUtil.isPrivateBoardName(boardName)) {
                        BoardIndexEntity board = (BoardIndexEntity) session.createQuery("from PoolIndexEntity where name=:name").setParameter("name", boardName).uniqueResult();
                        if (!board.getMembers().contains(currentAlias) && !board.getOwner().equals(currentAlias)) {
                            throw new NotAuthorizedException();
                        }
                    }
                    if (sinceEntryId == null) {
                        result = hibernateTemplate.find("from MessageEntity e where e.board.name = ? order by e.created desc ", boardName);
                    } else {
                        result = hibernateTemplate.find("from MessageEntity e where e.board.name = ? and e.created > ? order by e.created desc", boardName, fromDate);
                    }
                }
                Collections.reverse(result);
                if (currentAlias != null) {
                    session.refresh(currentAlias);
                    PositionEntity pe = (PositionEntity) session.createQuery("from PositionEntity pe where pe.boardName=:boardName and pe.user=:user").setParameter("boardName", boardName).setParameter("user", currentAlias).uniqueResult();
                    if (pe == null) {
                        pe = new PositionEntity();
                        pe.setResourceUri(boardName);
                        pe.setAlias(currentAlias);
                    }
                    pe.setLastRead(new Date());
                    session.saveOrUpdate(pe);
                }
                return result;
            }
        });
    }


    public MessageEntity createEntry(final String boardName, final String text, final AliasEntity alias, final MessageSource messageSource, final String externalId) {
        return hibernateTemplate.execute(new HibernateCallback<MessageEntity>() {
            @Override
            public MessageEntity doInHibernate(Session session) throws HibernateException, SQLException {
                final BoardIndexEntity boardIndexEntity = getOrCreateBoard(boardName);
                session.saveOrUpdate(boardIndexEntity);
                final MessageEntity message = new MessageEntity();
                session.merge(alias);
                message.setAuthor(alias);
                message.setMessageText(text);
                message.setCreated(new Date());
                message.setBoard(boardIndexEntity);
                message.setSource(messageSource);
                message.setExternalEntryURL(externalId);
                hibernateTemplate.saveOrUpdate(message);
                return message;
            }
        });
    }

     */

    @Override
    public List<BoardIndexEntity> getRecentBoards(final int from, final int size) {
        return sessionFactory.getCurrentSession().createCriteria(BoardIndexEntity.class).add(PUBLIC_BOARD).add(LISTED).addOrder(
                Order.desc("updated")
                                                                                                                               )
                             .setFirstResult(from)
                             .setMaxResults(size)
                             .list();
    }

    @Override
    public String getUniqueVisitorCount(final BoardIndexEntity board) {
        return sessionFactory.getCurrentSession().createQuery(
                "select count(distinct ve.visitor) from VisitEntity ve where ve.board= :board"
                                                             ).setParameter("board", board).uniqueResult().toString();
    }

    @Override
    public List<BoardIndexEntity> getUserBoards(final int from, final int size, final String aliasURI) {
        return sessionFactory.getCurrentSession()
                             .createQuery(
                                     "from BoardIndexEntity b where  b.listed=true and (b.owner.uri = :alias  or b.author.uri= :alias or b.creator.uri=:alias) order by b.updated desc"
                                         )
                             .setParameter("alias", aliasURI)
                             .setFirstResult(from)
                             .setMaxResults(size
                                           )
                             .list();
    }

    @Override
    public List<BoardIndexEntity> getVisitedBoards(final int from, final int size, final String aliasURI) {
        return sessionFactory.getCurrentSession().createQuery(
                "select v.board from VisitEntity v where v.visitor.uri= :visitor group by v.board order by max(v.created) desc"
                                                             )
                             .setParameter("visitor", aliasURI)
                             .setFirstResult(from)
                             .setMaxResults(size
                                           )
                             .list();
    }

    @Override
    public void saveBoard(final BoardIndexEntity board) {
        hibernateTemplate.saveOrUpdate(board);
    }

    public void setSessionFactory(final SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    /*
    public List<AliasEntity> getRecentUsers(final int max) {
        return hibernateTemplate.execute(new HibernateCallback<List<AliasEntity>>() {
            @Override
            public List<AliasEntity> doInHibernate(Session session) throws HibernateException, SQLException {
                hibernateTemplate.setMaxResults(max);
                List result;
                result = hibernateTemplate.find("select distinct e.author from MessageEntity e order by e.created desc");
//                Collections.reverse(result);
                return result;
            }
        });
    }

    public void addMemberToBoard(final BoardIndexEntity board, final AliasEntity alias) {
        hibernateTemplate.execute(new HibernateCallback<java.lang.Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.refresh(alias);
                session.refresh(board);
                board.getMembers().add(alias);
                return null;
            }
        });
    }
    */
}
