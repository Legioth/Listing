package org.vaadin.listing;

import java.lang.reflect.Constructor;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed.ItemAddEvent;
import com.vaadin.data.Container.Indexed.ItemRemoveEvent;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

public class Listing<T extends Component> {
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

    public interface ListingFactory<T> {
        public T createAndBind(Item item);
    }

    public interface IndexBasedLayout {
        public void add(Component component, int index);

        public void remove(int index);

        public int size();
    }

    private final Container.Ordered container;
    private ListingFactory<T> factory;

    private final IndexBasedLayout componentContainer;

    public Listing(IndexBasedLayout componentContainer,
            Container.Ordered container, ListingFactory<T> factory) {
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
                T component = createAndBindComponent(itemId);
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
            T component = createAndBindComponent(itemId);

            componentContainer.add(component, componentContainer.size());
        }
    }

    private T createAndBindComponent(Object itemId) {
        return factory.createAndBind(container.getItem(itemId));
    }

    private static <T extends Component> Listing<T> bind(
            IndexBasedLayout layout, Container.Ordered container,
            final Class<T> type) {
        try {
            final Constructor<T> constructor = type.getConstructor(Item.class);
            return new Listing<T>(layout, container, new ListingFactory<T>() {
                @Override
                public T createAndBind(Item item) {
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

    public static <T extends Component> Listing<T> bind(final CssLayout layout,
            Container.Ordered container, ListingFactory<T> factory) {
        return new Listing<T>(new CSSLayoutSupport(layout), container, factory);
    }

    public static <T extends Component> Listing<T> bind(
            final AbstractOrderedLayout layout, Container.Ordered container,
            ListingFactory<T> factory) {
        return new Listing<T>(new AOLSupport(layout), container, factory);
    }

    public static <T extends Component> Listing<T> bind(CssLayout layout,
            Container.Ordered container, Class<T> type) {
        return bind(new CSSLayoutSupport(layout), container, type);
    }

    public static <T extends Component> Listing<T> bind(
            AbstractOrderedLayout layout, Container.Ordered container,
            Class<T> type) {
        return bind(new AOLSupport(layout), container, type);
    }
}
