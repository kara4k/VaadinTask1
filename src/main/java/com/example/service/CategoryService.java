package com.example.service;

import com.example.entities.Category;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class CategoryService {

    private static final String NO_CATEGORY = "No category";

    private static CategoryService sCategoryService;

    private EntityManager mEntityManager = Persistence.createEntityManagerFactory("hotelsdb")
            .createEntityManager();

    private CategoryService() {
    }

    public static CategoryService getInstance() {
        if (sCategoryService == null) sCategoryService = new CategoryService();
        return sCategoryService;
    }

    public void save(Category category) {
        mEntityManager.getTransaction().begin();
        if (category.getId() == null) {
            if (isNameExist(category)) {
                mEntityManager.getTransaction().rollback();
                return;
            }
            mEntityManager.persist(category);
        } else {
            mEntityManager.merge(category);
        }
        mEntityManager.getTransaction().commit();
    }

    public void delete(Iterable<Category> categories) {
        mEntityManager.getTransaction().begin();
        categories.forEach(category -> {
            mEntityManager.remove(get(category.getId()));
        });
        mEntityManager.getTransaction().commit();
    }

    public Category get(long id) {
        return mEntityManager.find(Category.class, id);
    }

    public String getCategoryName(Category category) {
        if (category == null) return NO_CATEGORY;

        Category c = get(category.getId());
        return c == null ? NO_CATEGORY : c.getName();
    }

    public List<Category> getAll() {
        TypedQuery<Category> namedQuery = mEntityManager.createNamedQuery("Category.getAll", Category.class);
        return namedQuery.getResultList();
    }

    public boolean isNameExist(Category category) {
        TypedQuery<Category> namedQuery = mEntityManager.createNamedQuery("Category.findByName", Category.class);
        namedQuery.setParameter("name", category.getName());
        return !namedQuery.getResultList().isEmpty();
    }

}
