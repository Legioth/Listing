package org.vaadin.listing.builder;

import org.vaadin.listing.Listing;
import org.vaadin.listing.Listing.ListingFactory;

import com.vaadin.ui.Component;

public interface BuilderWithLayout {

    public <T extends Component> Listing<T> withViewier(
            ListingFactory<T> factory);

    public <T extends Component> Listing<T> withViewier(Class<T> viewerType);

}
