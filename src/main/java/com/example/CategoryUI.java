package com.example;

import com.example.entities.Category;
import com.example.service.CategoryService;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import java.util.List;
import java.util.Set;

public class CategoryUI extends VerticalLayout implements View {

    private HorizontalLayout controlsLayout = new HorizontalLayout();
    private HorizontalLayout contentLayout = new HorizontalLayout();

    private Button addCategory = new Button("Add category");
    private Button delCategory = new Button("Delete category");
    private Button editCategory = new Button("Edit category");
    private Grid<Category> mGrid = new Grid<>();
    private CategoryEditForm mEditForm = new CategoryEditForm(this);

    private CategoryService mCategoryService = CategoryService.getInstance();


    public CategoryUI(){
        setupLayout();
        setupListeners();
        updateList();
    }

    private void setupListeners() {
        mGrid.asMultiSelect().addSelectionListener((MultiSelectionListener<Category>) multiSelectionEvent -> {
            Set<Category> allSelectedItems = multiSelectionEvent.getAllSelectedItems();

            hideForm();
            if (allSelectedItems.isEmpty()){
                delCategory.setEnabled(false);
                editCategory.setEnabled(false);
            } else if (allSelectedItems.size() == 1) {
                delCategory.setEnabled(true);
                editCategory.setEnabled(true);
            } else {
                delCategory.setEnabled(true);
                editCategory.setEnabled(false);
            }
        });

        addCategory.addClickListener(e -> {
            mGrid.deselectAll();
            mEditForm.showForm(new Category());
        });

        delCategory.addClickListener(e -> {
            Set<Category> selectedItems = mGrid.asMultiSelect().getSelectedItems();
            if (selectedItems.isEmpty()) return;
            mCategoryService.delete(selectedItems);
            updateList();
        });

        editCategory.addClickListener(e -> {
            Set<Category> selectedItems = mGrid.asMultiSelect().getSelectedItems();
            if (selectedItems.isEmpty()) return;
            Category category = selectedItems.iterator().next();
            mEditForm.showForm(category);
        });
    }

    private void setupLayout() {
        controlsLayout.addComponents(addCategory, delCategory, editCategory);
        delCategory.setEnabled(false);
        editCategory.setEnabled(false);
        contentLayout.addComponents(mGrid, mEditForm);
        addComponents(controlsLayout, contentLayout);

        contentLayout.setWidth(100, Unit.PERCENTAGE);
        mGrid.setWidth(100, Unit.PERCENTAGE);
        mEditForm.setMargin(new MarginInfo(false, false, false, true));
        mEditForm.setVisible(false);
        contentLayout.setExpandRatio(mGrid, 3.0f);
        contentLayout.setExpandRatio(mEditForm, 1.0f);

        mGrid.addColumn(Category::getName).setCaption("Name");
        mGrid.setSelectionMode(Grid.SelectionMode.MULTI);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        updateList();
    }

    private void hideForm() {
        if (mEditForm.isVisible()) mEditForm.setVisible(false);
    }

    public void updateList() {
        List<Category> categorySet = mCategoryService.getAll();
        mGrid.setItems(categorySet);
    }
}
