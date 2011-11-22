package cazcade.fountain.index.persistence.dao;


import cazcade.fountain.index.persistence.entities.AliasEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Transactional
public class AliasDAOImpl implements AliasDAO {

    private final Logger log = LoggerFactory.getLogger(AliasDAOImpl.class);


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
    public AliasEntity getOrCreateUser(final String uri) {
        return hibernateTemplate.execute(new HibernateCallback<AliasEntity>() {
            @Override
            public AliasEntity doInHibernate(Session session) throws HibernateException, SQLException {
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
    public void forEachUser(UserDAOCallback userDAOCallback) {
        final List<AliasEntity> aliases = sessionFactory.getCurrentSession().createCriteria(AliasEntity.class).list();
        for (AliasEntity alias : aliases) {
            try {
                //move this into an executor
                userDAOCallback.process(alias);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                return;
            }
        }
    }


}
