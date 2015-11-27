package org.vaadin.listing.demo;

import javax.servlet.annotation.WebServlet;

import org.vaadin.listing.Listing;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("valo")
public class Demo extends UI {

    @WebServlet(value = "/*", asyncSupported = true, loadOnStartup = 1)
    @VaadinServletConfiguration(productionMode = false, ui = Demo.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        setContent(new Listing());
    }
}