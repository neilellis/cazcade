package cazcade.fountain.index.persistence.dao;


import cazcade.fountain.index.persistence.entities.AliasEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AliasDAO {

    @Transactional
    void saveUser(AliasEntity alias);

    List<AliasEntity> listUsers();

    @Transactional
    AliasEntity getOrCreateAlias(String uri);

    @Transactional
    void forEachUser(UserDAOCallback userDAOCallback);


    interface UserDAOCallback {
        void process(AliasEntity alias) throws Exception;
    }
}
