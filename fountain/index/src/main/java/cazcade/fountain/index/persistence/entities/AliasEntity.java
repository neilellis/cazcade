package cazcade.fountain.index.persistence.entities;


import cazcade.fountain.index.model.CommonBase;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

/**
 * @author neilellis@cazcade.com
 */
@Entity
@Table(name = "alias")
public class AliasEntity extends CommonBase {


    protected Boolean registered;
    protected String fullName;
    private Set<VisitEntity> visits;
    private Date lastEmailUpdateDate;

    @Id
    @Column(name = "uri", nullable = false)
    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(final String userName) {
        name = userName;
    }

    @Column(name = "fullname", nullable = true)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }


    @Column(name = "registered", nullable = true)
    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(final Boolean registered) {
        this.registered = registered;
    }


    @Column(name = "image_url", nullable = true)
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Column(name = "icon_url", nullable = true)
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(final String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


    public void setVisits(final Set<VisitEntity> visits) {
        this.visits = visits;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).
                toString();
    }

    public void setLastEmailUpdateDate(final Date lastEmailUpdateDate) {
        this.lastEmailUpdateDate = lastEmailUpdateDate;
    }

    @Column(name = "last_email_update", nullable = true)
    public Date getLastEmailUpdateDate() {
        return lastEmailUpdateDate;
    }
}


