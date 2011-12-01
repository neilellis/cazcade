package cazcade.fountain.index.persistence.dao;


import cazcade.common.Logger;
import cazcade.fountain.index.persistence.entities.AliasEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;

@Transactional
public class AliasDAOImpl implements AliasDAO {

    @Nonnull
    private final Logger log = Logger.getLogger(AliasDAOImpl.class);


    @Nonnull
    public static final String SYSTEM_USER = "hashbo";
    private HibernateTemplate hibernateTemplate;

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void saveUser(AliasEntity alias) {
        hibernateTemplate.saveOrUpdate(alias);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AliasEntity> listUsers() {
        return hibernateTemplate.find("from AliasEntity");
    }

    @Override
    public AliasEntity getOrCreateAlias(final String uri) {
        return hibernateTemplate.execute(new HibernateCallback<AliasEntity>() {
            @Nonnull
            @Override
            public AliasEntity doInHibernate(@Nonnull Session session) throws HibernateException, SQLException {
                String name = null;
                List users = hibernateTemplate.find("from AliasEntity e where e.uri = ?", uri);
                if (users.size() == 1) {
                    return (AliasEntity) users.get(0);
                } else if (users.size() == 0) {
                    final AliasEntity aliasEntity = new AliasEntity();
                    aliasEntity.setUri(uri);
                    session.persist(aliasEntity);
                    return aliasEntity;
                } else {
                    throw new RuntimeException("Too many matching user names.");
                }
            }
        });
    }

    @Override
    public void forEachUser(@Nonnull UserDAOCallback userDAOCallback) {
        final List<AliasEntity> aliases = sessionFactory.getCurrentSession().createCriteria(AliasEntity.class).list();
        for (AliasEntity alias : aliases) {
            try {
                //move this into an executor
                userDAOCallback.process(alias);
            } catch (InterruptedException e) {
                log.error(e);
                return;
            } catch (Exception e) {
                log.error(e);
            }
        }
    }


}
