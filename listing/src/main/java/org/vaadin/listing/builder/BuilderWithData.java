package org.vaadin.listing.builder;

import org.vaadin.listing.Listing;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.CssLayout;

public interface BuilderWithData<T extends Item> {

    BuilderWithLayout<T> inLayout(CssLayout layout);

    BuilderWithLayout<T> inLayout(AbstractOrderedLayout layout);

    Listing inDesign(AbstractOrderedLayout layout);

}
