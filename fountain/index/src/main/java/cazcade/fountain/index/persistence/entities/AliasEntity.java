package cazcade.fountain.index.persistence.entities;


import cazcade.fountain.index.model.CommonBase;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author neilellis@cazcade.com
 */
@Entity
@Table(name = "alias")
public class AliasEntity extends CommonBase implements Serializable {


    protected Boolean registered;
    protected String fullName;
    private Set<VisitEntity> visits;

    @Id
    @Column(name = "uri", nullable = false)
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String userName) {
        this.name = userName;
    }

    @Column(name = "fullname", nullable = true)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Column(name = "registered", nullable = true)
    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }


    @Column(name = "image_url", nullable = true)
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Column(name = "icon_url", nullable = true)
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @OneToMany(mappedBy = "visitor")
    public Set<VisitEntity> getVisits() {
        return this.visits;
    }

    public void setVisits(Set<VisitEntity> visits) {
        this.visits = visits;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this).
                toString();
    }
}


