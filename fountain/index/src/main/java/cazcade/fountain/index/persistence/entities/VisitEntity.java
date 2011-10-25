package cazcade.fountain.index.persistence.entities;


import cazcade.fountain.index.model.MessageBase;
import cazcade.fountain.index.model.MessageType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
@Entity
@Table(name = "visit")
public class VisitEntity extends MessageBase implements Serializable {

    private AliasEntity visitor;
    private BoardIndexEntity board;
    private MessageType type;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    @ManyToOne(targetEntity = AliasEntity.class)
    @JoinColumn(name = "visitor", nullable = false)
    public AliasEntity getVisitor() {
        return visitor;
    }

    public void setVisitor(AliasEntity visitor) {
        this.visitor = visitor;
    }

    @Column(name = "created", nullable = false)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Column(name = "deleted", nullable = true)
    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }



    @ManyToOne(targetEntity = BoardIndexEntity.class)
    @JoinColumn(name = "board", nullable = false)
    public BoardIndexEntity getBoard() {
        return board;
    }

    public void setBoard(BoardIndexEntity board) {
        this.board = board;
    }



}


