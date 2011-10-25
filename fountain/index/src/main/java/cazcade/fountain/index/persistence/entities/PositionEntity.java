package cazcade.fountain.index.persistence.entities;


import cazcade.fountain.index.model.PositionBase;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
@Entity
@Table(name = "position")
public class PositionEntity extends PositionBase {

    private AliasEntity alias;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id")
    public String getBoardPositionId() {
        return positionId;
    }

    public void setBoardPositionId(String boardPositionId) {
        this.positionId = boardPositionId;
    }


    @Column(name = "last_read", nullable = true)
    public Date getLastRead() {
        return this.lastRead;
    }

    public void setLastRead(Date lastRead) {
        this.lastRead = lastRead;
    }

    @Column(name = "last_write", nullable = true)
    public Date getLastWrote() {
        return lastWrote;
    }

    public void setLastWrote(Date lastWrote) {
        this.lastWrote = lastWrote;
    }

    @OneToOne(targetEntity = AliasEntity.class)
    @JoinColumn(name = "alias", nullable = false)
    public AliasEntity getAlias() {
        return alias;
    }

    public void setAlias(AliasEntity alias) {
        this.alias = alias;
    }

    @Column(name = "resource_uri", nullable = true)
    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String uri) {
        this.resourceUri = uri;
    }



}
