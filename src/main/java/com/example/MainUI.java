package com.example;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@SpringUI
public class MainUI extends UI {

    @WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    }

    @Configuration
    @EnableVaadin
    public static class MyConfiguration {
    }

    public static final String HOTELS = "";
    public static final String CATEGORY = "category";

    final Panel mPanel = new Panel();
    final Navigator navigator = new Navigator(this, mPanel);

    final VerticalLayout layout = new VerticalLayout();
    final MenuBar menuBar = new MenuBar();


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setContent(layout);
        layout.addComponents(menuBar, mPanel);

        menuBar.addItem("Hotel", VaadinIcons.BUILDING,
                (MenuBar.Command) menuItem -> navigator.navigateTo(HOTELS));
        menuBar.addItem("Category", VaadinIcons.ACADEMY_CAP,
                (MenuBar.Command) menuItem -> navigator.navigateTo(CATEGORY));
        menuBar.setStyleName(ValoTheme.MENUBAR_BORDERLESS);

        mPanel.setSizeFull();
        mPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);

        navigator.addView(HOTELS, new HotelUI());
        navigator.addView(CATEGORY, new CategoryUI());
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
