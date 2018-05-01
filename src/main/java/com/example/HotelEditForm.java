package com.example;

import com.example.entities.Category;
import com.example.entities.Hotel;
import com.example.service.CategoryService;
import com.example.service.HotelService;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.*;

public class HotelEditForm extends FormLayout {

    TextField name = new TextField("Name");
    TextField address = new TextField("Address");
    TextField rating = new TextField("Rating");
    DateField operatesFrom = new DateField("Date");
    NativeSelect<Category> category = new NativeSelect<>("Category");
    TextArea description = new TextArea("Description");
    TextField url = new TextField("URL");
    private Button save = new Button("Save");
    private Button close = new Button("Close");

    private HotelUI mUi;
    private HotelService mHotelService = HotelService.getInstance();
    private CategoryService mCategoryService = CategoryService.getInstance();
    private Hotel mHotel;
    private Binder<Hotel> mBinder = new Binder<>(Hotel.class);
    private HotelFormBinder mFormBinder = new HotelFormBinder();

    public HotelEditForm(HotelUI ui) {
        mUi = ui;

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.addComponents(save, close);

        buttonsLayout.setWidth(100, Unit.PERCENTAGE);
        name.setWidth(100, Unit.PERCENTAGE);
        address.setWidth(100, Unit.PERCENTAGE);
        rating.setWidth(100, Unit.PERCENTAGE);
        operatesFrom.setWidth(100, Unit.PERCENTAGE);
        category.setWidth(100, Unit.PERCENTAGE);
        description.setWidth(100, Unit.PERCENTAGE);
        url.setWidth(100, Unit.PERCENTAGE);
        save.setWidth(100, Unit.PERCENTAGE);
        close.setWidth(100, Unit.PERCENTAGE);

        addComponents(name, address, rating, operatesFrom, category, description, url, buttonsLayout);

        updateCategories();

        mFormBinder.bindForm(mBinder, this);

        save.addClickListener(e -> save());
        close.addClickListener(e -> close());
    }

    private void save() {
        if (mBinder.isValid()) {
            try {
                mBinder.writeBean(mHotel);
            } catch (ValidationException e) {
                Notification.show("Unable to save! " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }

            //NativeSelect empty value issue check
            Category c = mCategoryService.get(mHotel.getCategory().getId());
            if (c == null) {
                showUnableSaveMsg();
                return;
            }

            mHotelService.save(mHotel);
            mUi.updateList();
            close();
        } else {
            showUnableSaveMsg();
        }
    }

    private void showUnableSaveMsg() {
        Notification.show("Unable to save! Please review errors and fix them.",
                Notification.Type.ERROR_MESSAGE);
    }

    private void close() {
        setVisible(false);
    }

    private void updateCategories() {
        category.setItems(mCategoryService.getAll());
    }

    public void showHotel(Hotel hotel) {
        mHotel = hotel;
        mBinder.readBean(this.mHotel);
        updateCategories();
        categoryUpdateTrick(); // TODO: 27.04.2018
        setVisible(true);
    }

    private void categoryUpdateTrick() {
        if (mHotel.getCategory() != null) mBinder.readBean(this.mHotel);
    }
}
