package org.vaadin.listing;

import org.vaadin.listing.builder.BuilderWithData;
import org.vaadin.listing.builder.ListingBuilder;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed.ItemAddEvent;
import com.vaadin.data.Container.Indexed.ItemRemoveEvent;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Item;
import com.vaadin.ui.Component;

public class Listing {
    public interface ListingFactory {
        public Component createAndBind(Item item);
    }

    public interface IndexBasedLayout {
        public void add(Component component, int index);

        public void remove(int index);

        public int size();
    }

    private final Container.Ordered container;
    private ListingFactory factory;

    private final IndexBasedLayout componentContainer;

    public Listing(IndexBasedLayout componentContainer,
            Container.Ordered container, ListingFactory factory) {
        this.container = container;
        this.factory = factory;
        this.componentContainer = componentContainer;

        rebuild();

        if (container instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) container)
                    .addItemSetChangeListener(new ItemSetChangeListener() {
                        @Override
                        public void containerItemSetChange(
                                ItemSetChangeEvent event) {
                            handleItemSetEvent(event);
                        }
                    });
        }
    }

    private void handleItemSetEvent(ItemSetChangeEvent event) {
        if (event instanceof ItemAddEvent) {
            ItemAddEvent addEvent = (ItemAddEvent) event;
            int firstIndex = addEvent.getFirstIndex();
            Object itemId = addEvent.getFirstItemId();
            int addedItemsCount = addEvent.getAddedItemsCount();

            for (int i = 0; i < addedItemsCount; i++) {
                Component component = createAndBindComponent(itemId);
                componentContainer.add(component, firstIndex + i);
                itemId = container.nextItemId(itemId);
            }
        } else if (event instanceof ItemRemoveEvent) {
            ItemRemoveEvent removeEvent = (ItemRemoveEvent) event;
            int firstIndex = removeEvent.getFirstIndex();
            int removedItemsCount = removeEvent.getRemovedItemsCount();
            for (int i = 0; i < removedItemsCount; i++) {
                componentContainer.remove(firstIndex);
            }
        } else {
            // This could be tweaked to avoid rebuilding everything if most of
            // the items are still the same.
            rebuild();
        }
    }

    private void rebuild() {
        while (componentContainer.size() > 0) {
            componentContainer.remove(0);
        }

        for (Object itemId : container.getItemIds()) {
            Component component = createAndBindComponent(itemId);

            componentContainer.add(component, componentContainer.size());
        }
    }

    private Component createAndBindComponent(Object itemId) {
        return factory.createAndBind(container.getItem(itemId));
    }

    public static BuilderWithData ofContainer(final Container.Ordered container) {
        return new ListingBuilder(container);
    }
}
