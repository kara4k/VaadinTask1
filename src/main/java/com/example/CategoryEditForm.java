package com.example;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.*;

public class CategoryEditForm extends FormLayout {

    TextField mNameTF = new TextField("Name");
    Button mSaveBtn = new Button("Save");
    Button mCloseBtn = new Button("Close");

    private CategoryUI mUI;
    private Binder<Category> mBinder = new Binder<Category>(Category.class);
    private CategoryService mCategoryService = CategoryService.getInstance();
    private Category mCategory = new Category();

    public CategoryEditForm(CategoryUI ui) {
        mUI = ui;

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.addComponents(mSaveBtn, mCloseBtn);

        buttonsLayout.setWidth(100, Unit.PERCENTAGE);
        mNameTF.setWidth(100, Unit.PERCENTAGE);
        mSaveBtn.setWidth(100, Unit.PERCENTAGE);
        mCloseBtn.setWidth(100, Unit.PERCENTAGE);

        addComponents(mNameTF, buttonsLayout);

        mBinder.forField(mNameTF).bind(Category::getName, Category::setName);

        mSaveBtn.addClickListener(e -> save());
        mCloseBtn.addClickListener(e -> close());
    }

    private void close() {
        mCategory = new Category();
        setVisible(false);
    }

    private void save() {
        if (mBinder.isValid()) {
            try {
                mBinder.writeBean(mCategory);
            } catch (ValidationException e) {
                Notification.show("Unable to save! " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
            mCategoryService.save(mCategory);
            setVisible(false);
            mUI.updateList();
        } else {
            Notification.show("Unable to save! Please review errors and fix them.",
                    Notification.Type.ERROR_MESSAGE);
        }
    }

    public void showForm(Category category) {
        mCategory = category;
        mBinder.readBean(this.mCategory);
        setVisible(true);
    }
}
