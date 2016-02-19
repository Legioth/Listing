package org.vaadin.listing.builder;

import org.vaadin.listing.Listing;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.CssLayout;

public interface BuilderWithData {

    BuilderWithLayout inLayout(CssLayout layout);

    BuilderWithLayout inLayout(AbstractOrderedLayout layout);

    Listing<?> inDesign(AbstractOrderedLayout layout);

}
