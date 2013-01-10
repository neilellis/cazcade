/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.index.persistence.entities;


import cazcade.fountain.index.model.PositionBase;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
@Entity @Table(name = "position")
public class PositionEntity extends PositionBase {
    private AliasEntity alias;

    @Id @GeneratedValue(generator = "system-uuid") @GenericGenerator(name = "system-uuid", strategy = "uuid") @Column(name = "id")
    public String getBoardPositionId() {
        return positionId;
    }

    public void setBoardPositionId(final String boardPositionId) {
        positionId = boardPositionId;
    }

    @OneToOne(targetEntity = AliasEntity.class) @JoinColumn(name = "alias", nullable = false)
    public AliasEntity getAlias() {
        return alias;
    }

    public void setAlias(final AliasEntity alias) {
        this.alias = alias;
    }

    @Column(name = "last_read", nullable = true)
    public Date getLastRead() {
        return lastRead;
    }

    public void setLastRead(final Date lastRead) {
        this.lastRead = lastRead;
    }

    @Column(name = "last_write", nullable = true)
    public Date getLastWrote() {
        return lastWrote;
    }

    public void setLastWrote(final Date lastWrote) {
        this.lastWrote = lastWrote;
    }

    @Column(name = "resource_uri", nullable = true)
    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(final String uri) {
        resourceUri = uri;
    }
}
