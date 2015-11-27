package org.vaadin.listing.demo;

import org.vaadin.listing.Listing.ListingChild;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PersonViewer extends CustomComponent implements ListingChild {

    private final Label nameLabel = new Label();
    private final Label ageLabel = new Label();
    private final Label activeLabel = new Label();

    private final ValueChangeListener activeUpdater = new ValueChangeListener() {
        @Override
        public void valueChange(ValueChangeEvent event) {
            updateActive();
        }
    };

    private Item item;

    public PersonViewer() {
        nameLabel.setCaption("Name");
        ageLabel.setCaption("Age");
        activeLabel.setCaption("Active");

        nameLabel.setWidthUndefined();
        ageLabel.setWidthUndefined();
        activeLabel.setWidthUndefined();

        FormLayout formLayout = new FormLayout(nameLabel, ageLabel, activeLabel);
        formLayout.addStyleName("light");
        formLayout.setMargin(false);

        formLayout.setWidth("250px");

        VerticalLayout marginWrapper = new VerticalLayout(formLayout);
        marginWrapper.setMargin(true);

        Panel panel = new Panel(marginWrapper);
        panel.setWidthUndefined();
        setCompositionRoot(panel);

        setWidthUndefined();
        setPrimaryStyleName("personViewer");
    }

    @Override
    public void setItem(Item item) {
        if (this.item != null) {
            ((Property.ValueChangeNotifier) (Property<?>) this.item
                    .getItemProperty("active"))
                    .removeValueChangeListener(activeUpdater);
        }
        this.item = item;

        nameLabel.setPropertyDataSource(item.getItemProperty("name"));
        ageLabel.setPropertyDataSource(item.getItemProperty("age"));
        ((Property.ValueChangeNotifier) (Property<?>) item
                .getItemProperty("active"))
                .addValueChangeListener(activeUpdater);
        updateActive();
    }

    private void updateActive() {
        if (item == null) {
            activeLabel.setValue("");
        } else {
            Object value = item.getItemProperty("active").getValue();
            if (Boolean.TRUE.equals(value)) {
                activeLabel.setValue("Yes");
            } else {
                activeLabel.setValue("No");
            }
        }
    }

}
