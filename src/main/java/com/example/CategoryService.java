package com.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CategoryService {

    public static final String NO_CATEGORY = "No category";

    private static CategoryService sCategoryService;

    private String[] defaultNames = new String[]{"Hotel", "Hostel", "GuestHouse", "Appartments"};
    private Map<Integer, Category> mCategoryMap = new HashMap<>();
    private int mIndex = 0;

    private CategoryService() {
        for (int i = 0; i < defaultNames.length; i++) {
            Category category = new Category();
            category.setName(defaultNames[i]);
            category.setId(mIndex++);
            mCategoryMap.put(category.getId(), category);
        }
    }

    public static CategoryService getInstance() {
        if (sCategoryService == null) sCategoryService = new CategoryService();
        return sCategoryService;
    }

    public Set<Category> getCategorySet() {
        Set<Category> categories = new HashSet<>();

        mCategoryMap.forEach((integer, category) -> categories.add(category));
        return categories;
    }

    public Category getCategory(int id){
        return mCategoryMap.get(id);
    }

    public String getCategoryName(Category category) {
        Category c = mCategoryMap.get(category.getId());
        return c == null ? NO_CATEGORY : category.getName();
    }

    public void save(Category category) {
        if (category == null) return;
        if (category.getId() == null) category.setId(mIndex++);
        mCategoryMap.put(category.getId(), category);
    }

    public void delete(Iterable<Category> iterable) {
        iterable.forEach(category -> mCategoryMap.remove(category.getId()));
    }

    public int getCount() {
        return mCategoryMap.size();
    }
}
