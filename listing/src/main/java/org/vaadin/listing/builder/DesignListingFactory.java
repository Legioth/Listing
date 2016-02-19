package org.vaadin.listing.builder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.vaadin.listing.Listing.ListingFactory;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

public class DesignListingFactory implements ListingFactory<Component> {

    private Element itemComponentDesign;

    public DesignListingFactory(AbstractOrderedLayout layoutInDesign) {
        Component designRootComponent = findDesignRootComponent(layoutInDesign);

        Class<? extends Component> designRootClass = findClassWithAnnotation(
                designRootComponent.getClass(), DesignRoot.class);

        String fieldName = getFieldName(designRootClass, designRootComponent,
                layoutInDesign);

        // Stuff borrowed from Design.read
        DesignRoot designRootAnnotation = designRootClass
                .getAnnotation(DesignRoot.class);
        String filename = designRootAnnotation.value();
        if (filename.equals("")) {
            filename = designRootClass.getSimpleName() + ".html";
        }

        InputStream stream = designRootClass.getResourceAsStream(filename);
        if (stream == null) {
            throw new RuntimeException("Unable to find design file " + filename
                    + " in " + designRootClass.getPackage().getName());
        }

        Document document;
        try {
            document = parse(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Find the sub design corresponding to the provided layout
        Element rootLayoutElement = document.getElementsByAttributeValue("_id",
                fieldName).first();
        if (rootLayoutElement == null) {
            throw new RuntimeException("Could not find local id " + fieldName
                    + " in " + filename);
        }

        // Use the first child in the layout as a design for the items
        itemComponentDesign = rootLayoutElement.child(0);
    }

    private static String getFieldName(
            Class<? extends Component> componentType, Component instance,
            AbstractOrderedLayout fieldValue) {
        for (Field field : componentType.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(instance) == fieldValue) {
                    return field.getName().toLowerCase(Locale.ENGLISH);
                }
            } catch (Exception e) {
                throw new RuntimeException("Can't read field " + field, e);
            }
        }

        // No match
        throw new RuntimeException(
                "Couldn't find matching instance in design root");
    }

    private static Component findDesignRootComponent(Component componentInDesign) {
        while (componentInDesign != null) {
            Class<? extends Component> designRootClass = findClassWithAnnotation(
                    componentInDesign.getClass(), DesignRoot.class);
            if (designRootClass != null) {
                return componentInDesign;
            }

            componentInDesign = componentInDesign.getParent();
        }

        throw new IllegalArgumentException(
                "Component must be an ancestor of a component annotated with @"
                        + DesignRoot.class.getSimpleName());
    }

    @Override
    public Component createAndBind(Item item) {
        // Create
        DesignContext context = new DesignContext();
        Component itemComponent = context.readDesign(itemComponentDesign);

        // Bind
        for (Object propertyId : item.getItemPropertyIds()) {
            if (propertyId instanceof String) {
                String localId = (String) propertyId;
                Component localIdComponent = context
                        .getComponentByLocalId(localId);
                if (localIdComponent instanceof Property.Viewer) {
                    Property.Viewer viewer = (Property.Viewer) localIdComponent;
                    viewer.setPropertyDataSource(item
                            .getItemProperty(propertyId));
                }
            }
        }

        return itemComponent;
    }

    // Borrowed from Design.java
    private static Document parse(InputStream html) {
        try {
            Document doc = Jsoup.parse(html, "UTF-8", "", Parser.htmlParser());
            return doc;
        } catch (IOException e) {
            throw new DesignException("The html document cannot be parsed.");
        }
    }

    // Borrowed from Design.java
    private static Class<? extends Component> findClassWithAnnotation(
            Class<? extends Component> componentClass,
            Class<? extends Annotation> annotationClass) {
        if (componentClass == null) {
            return null;
        }

        if (componentClass.isAnnotationPresent(annotationClass)) {
            return componentClass;
        }

        Class<?> superClass = componentClass.getSuperclass();
        if (!Component.class.isAssignableFrom(superClass)) {
            return null;
        }

        return findClassWithAnnotation((Class<? extends Component>) superClass,
                annotationClass);
    }
}
