package cazcade.fountain.index.persistence.entities;


import cazcade.fountain.index.model.BoardType;
import cazcade.fountain.index.model.CommonBase;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.String;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author neilellis@cazcade.com
 */
@org.hibernate.annotations.Entity
@Entity
@Table(name = "board")
public class BoardIndexEntity extends CommonBase implements Serializable {

    private long commentCount;
    private long visitCount;
    private long likeCount;
    private long followerCount;
    private long activityCount;
    private long popularity;

    private Set<AliasEntity> members = new HashSet<AliasEntity>();
    private Set<AliasEntity> followers = new HashSet<AliasEntity>();
    private Set<VisitEntity> visits = new HashSet<VisitEntity>();
    private Set<MessageEntity> comments = new HashSet<MessageEntity>();
    private Set<MessageEntity> chats = new HashSet<MessageEntity>();
    protected BoardType type;
    protected AliasEntity creator;
    protected AliasEntity owner;
    protected AliasEntity author;
    protected String text;
    private boolean listed;


    @Id
    @Column(name = "uri", nullable = false)
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * The Short Name of the pool not the name attribute
     *
     * @return
     */
    @Column(name = "short_url", nullable = true)
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String boardName) {
        this.shortUrl = boardName;
    }

    @Column(name = "title", nullable = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(mappedBy = "board")
    public Set<MessageEntity> getComments() {
        return this.comments;
    }

    public void setComments(Set<MessageEntity> comments) {
        this.comments = comments;
    }

    @OneToMany(mappedBy = "board")
    public Set<VisitEntity> getVisits() {
        return this.visits;
    }

    public void setVisits(Set<VisitEntity> visits) {
        this.visits = visits;
    }


    @Column(name = "type", nullable = true)
    public BoardType getType() {
        return type;
    }

    public void setType(BoardType model) {
        this.type = model;
    }

    @Column(name = "text", nullable = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @ManyToOne(targetEntity = AliasEntity.class)
    @JoinColumn(name = "creator", nullable = true)
    public AliasEntity getCreator() {
        return creator;
    }

    public void setCreator(AliasEntity creator) {
        this.creator = creator;
    }

    @ManyToOne(targetEntity = AliasEntity.class)
    @JoinColumn(name = "owner", nullable = true)
    public AliasEntity getOwner() {
        return owner;
    }

    public void setOwner(AliasEntity owner) {
        this.owner = owner;
    }


    @ManyToOne(targetEntity = AliasEntity.class)
    @JoinColumn(name = "author", nullable = true)
    public AliasEntity getAuthor() {
        return author;
    }

    public void setAuthor(AliasEntity author) {
        this.author = author;
    }


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "members", joinColumns = {@JoinColumn(name = "resource_uri")}, inverseJoinColumns = {@JoinColumn(name = "member_uri")})
    public Set<AliasEntity> getMembers() {
        return members;
    }

    public void setMembers(Set<AliasEntity> members) {
        this.members = members;
    }

//    @Column(name = "BOARD_RATING", nullable = true)
//    public Double getRating() {
//        return rating;
//    }
//
//    public void setRating(Double rating) {
//        this.rating = rating;
//    }

    @Column(name = "comment_count", nullable = false)
    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    @Column(name = "visit_count", nullable = false)
    public long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(long visitCount) {
        this.visitCount = visitCount;
    }

    @Column(name = "like_count", nullable = false)
    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    @Column(name = "follower_count", nullable = false)
    public long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(long followerCount) {
        this.followerCount = followerCount;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "follow", joinColumns = {@JoinColumn(name = "followed_uri")}, inverseJoinColumns = {@JoinColumn(name = "follower_uri")})
    public Set<AliasEntity> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<AliasEntity> followers) {
        this.followers = followers;
    }

    @OneToMany(mappedBy = "board")
    public Set<MessageEntity> getChats() {
        return chats;
    }

    public void setChats(Set<MessageEntity> chats) {
        this.chats = chats;
    }


    public void incrementActivity() {
        activityCount++;
    }

    @Column(name = "activity_count", nullable = false)
    public long getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(long activityCount) {
        this.activityCount = activityCount;
    }


    @Column(name = "popularity", nullable = false)
    public long getPopularity() {
        return popularity;
    }

    public void setPopularity(long popularity) {
        this.popularity = popularity;
    }

    public void setListed(boolean listed) {
        this.listed = listed;
    }

    @Column(name = "listed", nullable = false)
    public boolean isListed() {
        return listed;
    }

    @Column(name = "updated", nullable = false)
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }


    public void incrementVisits() {
        visitCount++;
    }

    @Column(name = "created", nullable = false)
    public Date getCreated() {
        return created;
    }


    public void setCreated(Date created) {
        this.created = created;
    }
}




