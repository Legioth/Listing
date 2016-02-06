package org.vaadin.listing.builder;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.CssLayout;

public interface BuilderWithData {

    BuilderWithLayout inLayout(CssLayout layout);

    BuilderWithLayout inLayout(AbstractOrderedLayout layout);

}
