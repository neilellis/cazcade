/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.index.persistence.dao;


import cazcade.fountain.index.persistence.entities.AliasEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AliasDAO {
    @Transactional void forEachUser(UserDAOCallback userDAOCallback);

    @Transactional AliasEntity getOrCreateAlias(String uri);

    List<AliasEntity> listUsers();

    @Transactional void saveUser(AliasEntity alias);


    interface UserDAOCallback {
        void process(AliasEntity alias) throws Exception;
    }
}
