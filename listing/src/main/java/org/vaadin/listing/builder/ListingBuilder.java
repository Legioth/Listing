package org.vaadin.listing.builder;

import java.lang.reflect.Constructor;

import org.vaadin.listing.Listing;
import org.vaadin.listing.Listing.IndexBasedLayout;
import org.vaadin.listing.Listing.ListingFactory;

import com.vaadin.data.Container.Ordered;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

public class ListingBuilder implements BuilderWithData, BuilderWithLayout {
    private static final class CSSLayoutSupport implements IndexBasedLayout {
        private final CssLayout layout;

        private CSSLayoutSupport(CssLayout layout) {
            this.layout = layout;
        }

        @Override
        public int size() {
            return layout.getComponentCount();
        }

        @Override
        public void remove(int index) {
            layout.removeComponent(layout.getComponent(index));
        }

        @Override
        public void add(Component component, int index) {
            layout.addComponent(component, index);
        }
    }

    private static final class AOLSupport implements IndexBasedLayout {
        private final AbstractOrderedLayout layout;

        private AOLSupport(AbstractOrderedLayout layout) {
            this.layout = layout;
        }

        @Override
        public int size() {
            return layout.getComponentCount();
        }

        @Override
        public void remove(int index) {
            layout.removeComponent(layout.getComponent(index));
        }

        @Override
        public void add(Component component, int index) {
            layout.addComponent(component, index);
        }
    }

    private Ordered container;
    private IndexBasedLayout layout;

    public ListingBuilder(Ordered container) {
        this.container = container;
    }

    @Override
    public BuilderWithLayout inLayout(CssLayout layout) {
        this.layout = new CSSLayoutSupport(layout);
        return this;
    }

    @Override
    public BuilderWithLayout inLayout(AbstractOrderedLayout layout) {
        this.layout = new AOLSupport(layout);
        return this;
    }

    @Override
    public Listing withViewier(ListingFactory factory) {
        return new Listing(layout, container, factory);
    }

    @Override
    public Listing withViewier(Class<? extends Component> type) {
        try {
            final Constructor<? extends Component> constructor = type
                    .getConstructor(Item.class);
            return new Listing(layout, container, new ListingFactory() {
                @Override
                public Component createAndBind(Item item) {
                    try {
                        return constructor.newInstance(item);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(type.getName()
                    + " must have an (Item) constructor");
        }
    }

    @Override
    public Listing inDesign(AbstractOrderedLayout layoutInDesign) {
        return new Listing(new AOLSupport(layoutInDesign), container,
                new DesignListingFactory(layoutInDesign));
    }

}
