package com.example;

import com.example.entities.Category;
import com.example.entities.Hotel;
import com.example.service.CategoryService;
import com.example.service.HotelService;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValidationResult;
import com.vaadin.ui.*;

import java.util.List;

public class HotelEditForm extends FormLayout {

    TextField name = new TextField("Name");
    TextField address = new TextField("Address");
    TextField rating = new TextField("Rating");
    DateField operatesFrom = new DateField("Date");
    NativeSelect<Category> category = new NativeSelect<>("Category");
    PaymentField paymentField = new PaymentField();
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
        paymentField.setWidth(100, Unit.PERCENTAGE);
        description.setWidth(100, Unit.PERCENTAGE);
        url.setWidth(100, Unit.PERCENTAGE);
        save.setWidth(100, Unit.PERCENTAGE);
        close.setWidth(100, Unit.PERCENTAGE);

        addComponents(name, address, rating, operatesFrom, category, paymentField, description, url, buttonsLayout);

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
                Notification.show("Unable to save! Please review errors and fix them.",
                        Notification.Type.ERROR_MESSAGE);
                return;
            }

            mHotelService.save(mHotel);
            mUi.updateList();
            close();
        } else {
            showValidationError();
        }
    }

    private void showValidationError() {
        List<ValidationResult> validationErrors = mBinder.validate().getValidationErrors();
        if (!validationErrors.isEmpty()) {
            String errorMessage = validationErrors.get(0).getErrorMessage();
            Notification.show(errorMessage, Notification.Type.ERROR_MESSAGE);
        }
    }

    private void close() {
        setVisible(false);
    }

    private void updateCategories() {
        category.setItems(mCategoryService.getAll());
        category.setItemCaptionGenerator((ItemCaptionGenerator<Category>) Category::getName);
    }

    public void showHotel(Hotel hotel) {
        mHotel = hotel;
        updateCategories();
        mBinder.readBean(this.mHotel);
        setVisible(true);
    }
}
