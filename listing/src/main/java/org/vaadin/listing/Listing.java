package org.vaadin.listing;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class Listing extends CustomComponent {
    public Listing() {
        setCompositionRoot(new Label("Listing"));
    }
}
