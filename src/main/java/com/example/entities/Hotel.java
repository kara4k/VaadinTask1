package com.example.entities;

import org.hibernate.annotations.NotFound;

import javax.persistence.*;

@Entity
@Table(name = "HOTEL")
@NamedQueries({
        @NamedQuery(name = "Hotel.getAll", query = "SELECT c from Hotel c"),
        @NamedQuery(name = "Hotel.filter", query = "SELECT e FROM Hotel AS e " +
                "WHERE LOWER(e.name) LIKE :namefilter AND LOWER(e.address) LIKE :addressfilter")
})
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Version
    @Column(name = "OPTLOCK", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Integer version;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "RATING")
    private Integer rating;

    @Column(name = "OPERATES_FROM")
    private Long operatesFrom;

    @NotFound
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_HOTEL_CATEGORY"))
    private Category category;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "URL")
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Long getOperatesFrom() {
        return operatesFrom;
    }

    public void setOperatesFrom(Long operatesFrom) {
        this.operatesFrom = operatesFrom;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
