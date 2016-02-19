package org.vaadin.listing.builder;

import org.vaadin.listing.Listing;
import org.vaadin.listing.Listing.ListingFactory;

import com.vaadin.ui.Component;

public interface BuilderWithLayout {

    public Listing withViewier(ListingFactory factory);

    public Listing withViewier(Class<? extends Component> viewerType);

}
