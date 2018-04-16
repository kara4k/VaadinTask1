package com.example;

import com.vaadin.data.Binder;
import com.vaadin.ui.*;

public class HotelEditForm extends FormLayout {

    private MyUI mUi;
    private HotelService mHotelService = HotelService.getInstance();
    private Hotel mHotel;
    private Binder<Hotel> mBinder = new Binder<>(Hotel.class);

    private TextField name = new TextField("Name");
    private TextField address = new TextField("Address");
    private TextField rating = new TextField("Rating");
    private DateField operatesFrom = new DateField("Date");
    private NativeSelect<HotelCategory> category = new NativeSelect<>("Category");
    private TextField url = new TextField("URL");
    private TextArea description = new TextArea("Description");

    private Button save = new Button("Save");
    private Button close = new Button("Close");

    public HotelEditForm(MyUI ui) {
        mUi = ui;

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.addComponents(save, close);

        category.setItems(HotelCategory.values());

        addComponents(name, address, rating, operatesFrom, category, url, buttonsLayout, description);
        mBinder.bindInstanceFields(this);

        save.addClickListener(e -> save());
        close.addClickListener(e -> close());
    }

    private void save() {
        mHotelService.save(mHotel);
        mUi.updateList();
        close();
    }

    private void close() {
        setVisible(false);
    }

    public Hotel getHotel() {
        return mHotel;
    }

    public void setHotel(Hotel hotel) {
        mHotel = hotel;
        mBinder.setBean(this.mHotel);
        setVisible(true);
    }
}
