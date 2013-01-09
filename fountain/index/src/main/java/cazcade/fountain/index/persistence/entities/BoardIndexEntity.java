package cazcade.fountain.index.persistence.entities;


import cazcade.fountain.index.model.BoardType;
import cazcade.fountain.index.model.CommonBase;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author neilellis@cazcade.com
 */
@org.hibernate.annotations.Entity
@Entity
@Table(name = "board")
public class BoardIndexEntity extends CommonBase {
    protected BoardType type;
    protected AliasEntity creator;
    protected AliasEntity owner;
    protected AliasEntity author;
    protected String text;

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
    private boolean listed;


    public void incrementActivity() {
        activityCount++;
    }

    public void incrementVisits() {
        visitCount++;
    }

    @Column(name = "activity_count", nullable = false)
    public long getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(final long activityCount) {
        this.activityCount = activityCount;
    }

    @ManyToOne(targetEntity = AliasEntity.class)
    @JoinColumn(name = "author", nullable = true)
    public AliasEntity getAuthor() {
        return author;
    }

    public void setAuthor(final AliasEntity author) {
        this.author = author;
    }

    @OneToMany(mappedBy = "board")
    public Set<MessageEntity> getChats() {
        return chats;
    }

    public void setChats(final Set<MessageEntity> chats) {
        this.chats = chats;
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

    public void setCommentCount(final long commentCount) {
        this.commentCount = commentCount;
    }

    @OneToMany(mappedBy = "board")
    public Set<MessageEntity> getComments() {
        return comments;
    }

    public void setComments(final Set<MessageEntity> comments) {
        this.comments = comments;
    }

    @Column(name = "created", nullable = false)
    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    @ManyToOne(targetEntity = AliasEntity.class)
    @JoinColumn(name = "creator", nullable = true)
    public AliasEntity getCreator() {
        return creator;
    }

    public void setCreator(final AliasEntity creator) {
        this.creator = creator;
    }

    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Column(name = "follower_count", nullable = false)
    public long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(final long followerCount) {
        this.followerCount = followerCount;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "follow", joinColumns = {@JoinColumn(name = "followed_uri")},
               inverseJoinColumns = {@JoinColumn(name = "follower_uri")})
    public Set<AliasEntity> getFollowers() {
        return followers;
    }

    public void setFollowers(final Set<AliasEntity> followers) {
        this.followers = followers;
    }

    @Column(name = "like_count", nullable = false)
    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(final long likeCount) {
        this.likeCount = likeCount;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "members", joinColumns = {@JoinColumn(name = "resource_uri")},
               inverseJoinColumns = {@JoinColumn(name = "member_uri")})
    public Set<AliasEntity> getMembers() {
        return members;
    }

    public void setMembers(final Set<AliasEntity> members) {
        this.members = members;
    }

    @ManyToOne(targetEntity = AliasEntity.class)
    @JoinColumn(name = "owner", nullable = true)
    public AliasEntity getOwner() {
        return owner;
    }

    public void setOwner(final AliasEntity owner) {
        this.owner = owner;
    }

    @Column(name = "popularity", nullable = false)
    public long getPopularity() {
        return popularity;
    }

    public void setPopularity(final long popularity) {
        this.popularity = popularity;
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

    public void setShortUrl(final String boardName) {
        shortUrl = boardName;
    }

    @Column(name = "text", nullable = true)
    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    @Column(name = "title", nullable = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Column(name = "type", nullable = true)
    public BoardType getType() {
        return type;
    }

    public void setType(final BoardType model) {
        type = model;
    }

    @Column(name = "updated", nullable = false)
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(@Nullable final Date updated) {
        this.updated = updated;
    }

    @Id
    @Column(name = "uri", nullable = false)
    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    @Column(name = "visit_count", nullable = false)
    public long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(final long visitCount) {
        this.visitCount = visitCount;
    }

    @OneToMany(mappedBy = "board")
    public Set<VisitEntity> getVisits() {
        return visits;
    }

    public void setVisits(final Set<VisitEntity> visits) {
        this.visits = visits;
    }

    @Column(name = "listed", nullable = false)
    public boolean isListed() {
        return listed;
    }

    public void setListed(final boolean listed) {
        this.listed = listed;
    }
}




