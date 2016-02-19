package org.vaadin.listing.builder;

import org.vaadin.listing.Listing;
import org.vaadin.listing.Listing.ListingFactory;

import com.vaadin.data.Item;
import com.vaadin.ui.Component;

public interface BuilderWithLayout<T extends Item> {

    public Listing withViewier(ListingFactory<T> factory);

    public Listing withViewier(Class<? extends Component> viewerType);

}
