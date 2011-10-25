package cazcade.fountain.index.persistence.entities;


import cazcade.fountain.index.model.MessageBase;
import cazcade.fountain.index.model.MessageSource;
import cazcade.fountain.index.model.MessageType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.String;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
@Entity
@Table(name = "message")
public class MessageEntity extends MessageBase implements Serializable {

    private AliasEntity author;
    private BoardIndexEntity board;
    private MessageType type;

    @Id
    @Column(name = "uri")
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    @Column(name = "text", nullable = false)
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String entryText) {
        this.messageText = entryText;
    }

    @ManyToOne(targetEntity = AliasEntity.class)
    @JoinColumn(name = "author", nullable = false)
    public AliasEntity getAuthor() {
        return author;
    }

    public void setAuthor(AliasEntity author) {
        this.author = author;
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


    @Column(name = "external_url", nullable = true)
    public String getExternalEntryURL() {
        return externalEntryURL;
    }

    public void setExternalEntryURL(String externalEntryId) {
        this.externalEntryURL = externalEntryId;
    }

    @Column(name = "source", nullable = true, columnDefinition = "int(1) default 0")
    public MessageSource getSource() {
        return source;
    }

    public void setSource(MessageSource source) {
        this.source = source;
    }

    @ManyToOne(targetEntity = BoardIndexEntity.class)
    @JoinColumn(name = "board", nullable = true)
    public BoardIndexEntity getBoard() {
        return board;
    }

    public void setBoard(BoardIndexEntity board) {
        this.board = board;
    }

    @Column(name = "type", nullable = false)
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}


