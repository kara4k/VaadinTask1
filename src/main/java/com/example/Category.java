package com.example;

public class Category {
    private Integer mId;
    private String mName = "";

    public Category() {
    }

    public Category(Integer id, String name) {
        mId = id;
        mName = name;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName == null ? "" : mName;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Category)
                && ((Category) obj).getName().toLowerCase().equals(this.mName.toLowerCase());
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }
}
