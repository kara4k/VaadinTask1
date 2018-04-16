package com.example;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;
import java.util.List;
import java.util.Locale;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    final VerticalLayout layout = new VerticalLayout();
    final HorizontalLayout controlsLayout = new HorizontalLayout();
    final HotelService hotelService = HotelService.getInstance();
    final Grid<Hotel> hotelGrid = new Grid<>(Hotel.class);
    final TextField filterNameTF = new TextField("Name");
    final TextField filterAdressTF = new TextField("Address");
    final Button addHotelBtn = new Button("Add hotel");
    final HorizontalLayout contentLayout = new HorizontalLayout();
    final Button deleteHotelBtn = new Button("Delete hotel");
    final VerticalLayout mainLayout = new VerticalLayout();
    final HorizontalLayout filterLayout = new HorizontalLayout();
    final HotelEditForm hotelForm = new HotelEditForm(this);
    final Link link = new Link();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setContent(layout);

        setupLayout();
        setupListeners();
        updateList();
    }

    private void setupLayout() {
        controlsLayout.addComponents(addHotelBtn, deleteHotelBtn);
        mainLayout.addComponents(hotelGrid, controlsLayout, link);
        contentLayout.addComponents(mainLayout, hotelForm);
        filterLayout.addComponents(filterNameTF, filterAdressTF);
        layout.addComponents(filterLayout, contentLayout);

        hotelForm.setVisible(false);
        deleteHotelBtn.setEnabled(false);
        hideLink();
    }

    private void setupListeners() {
        hotelGrid.setColumnOrder("name", "address", "rating", "category", "description");
        hotelGrid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                deleteHotelBtn.setEnabled(true);

                Hotel selectedHotel = e.getValue();
                hotelForm.setHotel(selectedHotel);

                showLink(selectedHotel);
            } else {
                hideLink();
            }
        });

        deleteHotelBtn.addClickListener(e -> {
            Hotel delCandidate = hotelGrid.getSelectedItems().iterator().next();
            hotelService.delete(delCandidate);
            deleteHotelBtn.setEnabled(false);
            updateList();
            hotelForm.setVisible(false);
            hideLink();
        });

        filterNameTF.addValueChangeListener(e -> updateList());
        filterNameTF.setValueChangeMode(ValueChangeMode.LAZY);
        filterAdressTF.addValueChangeListener(e -> updateList());
        filterAdressTF.setValueChangeMode(ValueChangeMode.LAZY);

        addHotelBtn.addClickListener(e -> hotelForm.setHotel(new Hotel()));
    }

    private void showLink(Hotel selectedHotel) {
        String url = selectedHotel.getUrl();

        if (url == null || selectedHotel.getUrl().isEmpty()) {
            hideLink();
            return;
        }

        link.setVisible(true);
        link.setCaption(String.format(Locale.ENGLISH, "Visit %s on booking.com", selectedHotel.getName()));
        link.setResource(new ExternalResource(selectedHotel.getUrl()));
    }

    public void hideLink() {
        link.setVisible(false);
    }

    public void updateList() {
        List<Hotel> hotelList = hotelService.findAll(filterNameTF.getValue(), filterAdressTF.getValue());
        hotelGrid.setItems(hotelList);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
