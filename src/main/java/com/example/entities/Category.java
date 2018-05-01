package com.example.entities;

import javax.persistence.*;

@Entity
@Table(name = "CATEGORIES")
@NamedQueries({
        @NamedQuery(name = "Category.getAll", query = "SELECT c from Category c"),
        @NamedQuery(name = "Category.findByName", query = "SELECT c FROM Category c WHERE c.name = :name")
})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Version
    @Column(name = "OPTLOCK", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Integer version;

    @Column(name = "NAME")
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

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

    @Override
    public String toString() {
        return name == null ? "" : name;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Category)
                && ((Category) obj).getName().toLowerCase().equals(this.name.toLowerCase());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}




