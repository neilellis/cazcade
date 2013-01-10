/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.index.persistence.entities;


import cazcade.fountain.index.model.MessageBase;
import cazcade.fountain.index.model.MessageSource;
import cazcade.fountain.index.model.MessageType;

import javax.persistence.*;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
@Entity @Table(name = "message")
public class MessageEntity extends MessageBase {
    private AliasEntity      author;
    private BoardIndexEntity board;
    private MessageType      type;

    @ManyToOne(targetEntity = AliasEntity.class) @JoinColumn(name = "author", nullable = false)
    public AliasEntity getAuthor() {
        return author;
    }

    public void setAuthor(final AliasEntity author) {
        this.author = author;
    }

    @ManyToOne(targetEntity = BoardIndexEntity.class) @JoinColumn(name = "board", nullable = true)
    public BoardIndexEntity getBoard() {
        return board;
    }

    public void setBoard(final BoardIndexEntity board) {
        this.board = board;
    }

    @Column(name = "created", nullable = false)
    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    @Column(name = "deleted", nullable = true)
    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(final Date deleted) {
        this.deleted = deleted;
    }

    @Column(name = "external_url", nullable = true)
    public String getExternalEntryURL() {
        return externalEntryURL;
    }

    public void setExternalEntryURL(final String externalEntryId) {
        externalEntryURL = externalEntryId;
    }

    @Column(name = "text", nullable = false)
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(final String entryText) {
        messageText = entryText;
    }

    @Column(name = "source", nullable = true, columnDefinition = "int(1) default 0")
    public MessageSource getSource() {
        return source;
    }

    public void setSource(final MessageSource source) {
        this.source = source;
    }

    @Column(name = "type", nullable = false)
    public MessageType getType() {
        return type;
    }

    public void setType(final MessageType type) {
        this.type = type;
    }

    @Id @Column(name = "uri")
    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }
}


