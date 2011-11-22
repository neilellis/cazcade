package cazcade.fountain.index.persistence.dao;


import cazcade.fountain.index.persistence.entities.AliasEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AliasDAO {

    @Transactional
    public void saveUser(AliasEntity alias);

    public List<AliasEntity> listUsers();

    @Transactional
    AliasEntity getOrCreateUser(String uri);

    @Transactional
    void forEachUser(UserDAOCallback userDAOCallback);


    interface UserDAOCallback {
        void process(AliasEntity alias) throws InterruptedException;
    }
}
