package org.vaadin.listing.demo;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.annotation.WebServlet;

import org.vaadin.listing.Listing;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("valo")
public class Demo extends UI {

    @WebServlet(value = "/*", asyncSupported = true, loadOnStartup = 1)
    @VaadinServletConfiguration(productionMode = false, ui = Demo.class)
    public static class Servlet extends VaadinServlet {
    }

    private Random random = new Random(0);

    @Override
    protected void init(VaadinRequest request) {
        // Too lazy to create a theme
        getPage().getStyles().add(".personViewer {margin: 0 10px 10px 0}");

        BeanItemContainer<Person> persons = new BeanItemContainer<>(
                Person.class, generatePersons(random, 10));

        CssLayout layout = new CssLayout();

        // Suitable width for 2 columns
        layout.setWidth("575px");
        // VerticalLayout layout = new VerticalLayout();


        Listing.ofContainer(persons).inLayout(layout)
                .withViewier(PersonViewer.class);
        // Alternative with lambda
        // Listing.ofContainer(persons).inLayout(layout)
        // .withViewier(PersonViewer::new);

        VerticalLayout rootLayout = new VerticalLayout(buildActions(persons),
                layout);
        rootLayout.setSpacing(true);
        rootLayout.setMargin(true);

        setContent(rootLayout);
    }

    private Component buildActions(BeanItemContainer<Person> container) {
        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setSpacing(true);

        actionLayout.addComponent(new Button("Add", e -> container.addItemAt(0,
                createPerson(random))));

        actionLayout.addComponent(new Button("Remove", e -> container
                .removeItem(container.getIdByIndex(0))));

        actionLayout.addComponent(new Button("Modify",
                e -> editFirstPerson(container)));

        return actionLayout;
    }

    private static void editFirstPerson(BeanItemContainer<Person> container) {
        BeanItem<Person> firstPerson = container.getItem(container
                .getIdByIndex(0));

        firstPerson.getItemProperty("name").setValue("Edited");
    }

    private static List<Person> generatePersons(Random random, int count) {
        return Stream.generate(() -> createPerson(random)).limit(count)
                .collect(Collectors.toList());
    }

    private static Person createPerson(Random random) {
        String name = "Person #" + random.nextInt(10000);
        int age = 20 + random.nextInt(50);
        boolean active = random.nextDouble() > 0.2;
        return new Person(name, age, active);
    }
}