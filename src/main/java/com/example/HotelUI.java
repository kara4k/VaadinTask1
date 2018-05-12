package com.example;

import com.example.entities.Hotel;
import com.example.service.CategoryService;
import com.example.service.HotelService;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;

import java.util.List;
import java.util.Set;

public class HotelUI extends VerticalLayout implements View {

    final HorizontalLayout controlsLayout = new HorizontalLayout();
    final HorizontalLayout contentLayout = new HorizontalLayout();
    final TextField filterNameTF = new TextField();
    final TextField filterAdressTF = new TextField();
    final Button addHotelBtn = new Button("Add hotel");
    final Button deleteHotelBtn = new Button("Delete hotel");
    final Button editHotelBtn = new Button("Edit hotel");
    final Button bulkUpdateBtn = new Button("Bulk Update");
    final Grid<Hotel> hotelGrid = new Grid<>();
    final HotelEditForm hotelForm = new HotelEditForm(this);
    final UpdatePopup updatePopup = new UpdatePopup(this);

    final HotelService hotelService = HotelService.getInstance();
    final CategoryService mCategoryService = CategoryService.getInstance();

    public HotelUI() {
        setupLayout();
        setupListeners();
        updateList();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        updateList();
    }

    private void setupLayout() {

        controlsLayout.addComponents(filterNameTF, filterAdressTF, addHotelBtn,
                deleteHotelBtn, editHotelBtn, bulkUpdateBtn);
        deleteHotelBtn.setEnabled(false);
        editHotelBtn.setEnabled(false);
        bulkUpdateBtn.setEnabled(false);
        contentLayout.addComponents(hotelGrid, hotelForm);
        updatePopup.setPopupVisible(false);

        addComponents(controlsLayout, contentLayout, updatePopup);
        setComponentAlignment(updatePopup, Alignment.MIDDLE_CENTER);

        contentLayout.setWidth(100, Unit.PERCENTAGE);
        hotelGrid.setWidth(100, Unit.PERCENTAGE);
        hotelForm.setMargin(new MarginInfo(false, false, false, true));
        hotelForm.setVisible(false);
        contentLayout.setExpandRatio(hotelGrid, 3.0f);
        contentLayout.setExpandRatio(hotelForm, 1.0f);

        hotelGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        hotelGrid.addColumn(Hotel::getName).setCaption("Name");
        hotelGrid.addColumn(Hotel::getAddress).setCaption("Address");
        hotelGrid.addColumn(Hotel::getRating).setCaption("Rating");
        hotelGrid.addColumn(this::getCategory).setCaption("Category");
        hotelGrid.addColumn(HotelFormBinder::getDate).setCaption("Operates From");
        hotelGrid.addColumn(Hotel::getDescription).setCaption("Description");
        Grid.Column<Hotel, String> linkColumn = hotelGrid.addColumn(hotel ->
                        "<a href='" + hotel.getUrl() + "' target='_target'>Hotel info</a>",
                new HtmlRenderer());
        linkColumn.setCaption("Url");

        filterNameTF.setPlaceholder("filter by name");
        filterNameTF.setValueChangeMode(ValueChangeMode.LAZY);
        filterAdressTF.setPlaceholder("filter by address");
        filterAdressTF.setValueChangeMode(ValueChangeMode.LAZY);

    }

    private String getCategory(Hotel hotel) {
        return mCategoryService.getCategoryName(hotel.getCategory());
    }

    private void setupListeners() {
        hotelGrid.asMultiSelect().addSelectionListener((MultiSelectionListener<Hotel>) multiSelectionEvent -> {
            Set<Hotel> allSelectedItems = multiSelectionEvent.getAllSelectedItems();

            hideForm();
            if (allSelectedItems.isEmpty()) {
                deleteHotelBtn.setEnabled(false);
                editHotelBtn.setEnabled(false);
                bulkUpdateBtn.setEnabled(false);
            } else if (allSelectedItems.size() == 1) {
                deleteHotelBtn.setEnabled(true);
                editHotelBtn.setEnabled(true);
                bulkUpdateBtn.setEnabled(false);
            } else {
                deleteHotelBtn.setEnabled(true);
                editHotelBtn.setEnabled(false);
                bulkUpdateBtn.setEnabled(true);
            }
        });

        addHotelBtn.addClickListener(e -> {
            hotelGrid.deselectAll();
            hotelForm.showHotel(new Hotel());
        });

        deleteHotelBtn.addClickListener(e -> {
            Set<Hotel> selectedItems = hotelGrid.getSelectedItems();
            if (selectedItems.isEmpty()) return;
            hotelService.delete(selectedItems);
            updateList();
        });

        editHotelBtn.addClickListener(e -> {
            Set<Hotel> selectedItems = hotelGrid.asMultiSelect().getSelectedItems();
            if (selectedItems.isEmpty()) return;
            Hotel hotel = selectedItems.iterator().next();
            hotelForm.showHotel(hotel);
        });

        bulkUpdateBtn.addClickListener(e -> {
            updatePopup.show(hotelGrid.getSelectedItems());
        });

        filterNameTF.addValueChangeListener(e -> updateList());
        filterAdressTF.addValueChangeListener(e -> updateList());
    }

    private void hideForm() {
        if (hotelForm.isVisible()) hotelForm.setVisible(false);
    }

    public void updateList() {
        List<Hotel> hotelList = hotelService.getAll(filterNameTF.getValue(), filterAdressTF.getValue());
        hotelGrid.setItems(hotelList);
    }

    public void onUpdateHotels(Set<Hotel> hotels) {
        hotelService.updateHotels(hotels);
        updateList();
    }
}
