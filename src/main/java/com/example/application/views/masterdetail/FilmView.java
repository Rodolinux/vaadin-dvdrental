package com.example.application.views.masterdetail;

import com.example.application.data.Film;
import com.example.application.services.FilmService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("CRUD de películas")
@Route(value = "films/:filmId?/:action?(edit)")
@Menu(order = 1, icon = "las la-film")
@Uses(Icon.class)
public class FilmView extends Div implements BeforeEnterObserver {

    private final String FILM_ID = "filmId";
    private final String FILM_EDIT_ROUTE_TEMPLATE = "films/%s/edit";

    private final Grid<Film> grid = new Grid<>(Film.class, false);
    private TextField title;
    private TextField description;

    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");

    private final BeanValidationBinder<Film> binder;

    private Film film;

    private final FilmService filmService;

    public FilmView(FilmService filmService) {
        this.filmService = filmService;
        addClassNames("film-view");
        setSizeFull();
        SplitLayout splitLayout = new SplitLayout();
        configureGrid();
        add(grid);
        updateList();

        //createGridLayout(splitLayout);
        //createEditorLayout(splitLayout);
        //add(splitLayout);

        //grid.addColumn("filmId").setAutoWidth(true);
        //grid.addColumn("title").setAutoWidth(true);
        //grid.addColumn("releaseYear").setAutoWidth(true);

        //grid.setItems(query -> filmService.getAllFilms(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        //grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(FILM_EDIT_ROUTE_TEMPLATE, event.getValue().getFilmId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(FilmView.class);
            }
        });

        binder = new BeanValidationBinder<>(Film.class);
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.film == null) {
                    this.film = new Film();
                }
                binder.writeBean(this.film);
                filmService.save(this.film);
                clearForm();
                refreshGrid();
                Notification.show("Película guardada");
                UI.getCurrent().navigate(FilmView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show("Error al guardar: Registro modificado por otro usuario.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Error: Verifique los datos ingresados.");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> filmId = event.getRouteParameters().get(FILM_ID).map(Long::parseLong);
        if (filmId.isPresent()) {
            Optional<Film> filmFromBackend = filmService.getFilmById(filmId.get().intValue());
            if (filmFromBackend.isPresent()) {
                populateForm(filmFromBackend.get());
            } else {
                Notification.show(String.format("Película no encontrada, ID = %s", filmId.get()), 3000, Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(FilmView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        title = new TextField("Título");
        description = new TextField("Descripción");
        formLayout.add(title, description);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Film value) {
        this.film = value;
        binder.readBean(this.film);
    }
    private void configureGrid() {
        grid.addClassNames("film-grid"); // Si usas estilos
        grid.setSizeFull();
        // Configura las columnas que quieres mostrar
        grid.setColumns("title", "releaseYear", "rentalDuration", "rentalRate", "rating", "lastUpdate");
        // Ajusta la columna de Language para mostrar el nombre
        grid.addColumn(film -> film.getLanguage() != null ? film.getLanguage().getName() : "N/A")
                .setHeader("Language").setSortable(true);
        // Oculta columnas que no quieres mostrar directamente en el Grid
        grid.getColumns().forEach(col -> {
            if (col.getKey() != null &&
                    (col.getKey().equals("filmId") || col.getKey().equals("description") ||
                            col.getKey().equals("length") || col.getKey().equals("replacementCost") ||
                            col.getKey().equals("specialFeatures"))) {
                col.setVisible(false);
            }
        });


        // Configurar el DataProvider con paginación
        DataProvider<Film, Void> dataProvider = DataProvider.fromCallbacks(
                // El "fetchCallback" para obtener una página de datos
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    // Convertir la información de ordenación de Vaadin a Spring Data Sort
                    Sort sort = Sort.unsorted();
                    if (!query.getSortOrders().isEmpty()) {
                        List<Sort.Order> orders = query.getSortOrders().stream()
                                .map(sortOrder -> sortOrder.getDirection() == com.vaadin.flow.data.provider.SortDirection.ASCENDING ?
                                        Sort.Order.asc(sortOrder.getSorted()) : Sort.Order.desc(sortOrder.getSorted()))
                                .collect(Collectors.toList());
                        sort = Sort.by(orders);
                    }

                    // Crear un objeto PageRequest con la paginación y ordenación
                    Pageable pageable = PageRequest.of(offset / limit, limit, sort);

                    // Llamar al servicio con la información de paginación
                    return filmService.list(pageable).stream();
                },
                // El "countCallback" para obtener el número total de elementos
                query -> (int) filmService.count()
        );

        grid.setDataProvider(dataProvider);
    }

    private void updateList() {
        // Refresca los datos del grid
        grid.getDataProvider().refreshAll();
    }
}