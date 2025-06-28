package com.example.application.views.masterdetail;

import com.example.application.data.Film;
import com.example.application.data.Language;
import com.example.application.data.MpaaRating;
import com.example.application.services.FilmService;
import com.example.application.services.LanguageService; // Importar LanguageService
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder; // Importar Binder
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter; // Importar Converter
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@PageTitle("CRUD de Películas")
@Route(value = "films/:filmId?/:action?(edit)") // Ruta para películas, con parámetro opcional para ID y acción
@Menu(order = 0, icon = "la la-film") // Icono de película, puedes cambiarlo
@RouteAlias("films") // Alias para la ruta base
@Uses(Icon.class)
public class FilmView extends Div implements BeforeEnterObserver {

    private final String FILM_ID = "filmId";
    private final String FILM_EDIT_ROUTE_TEMPLATE = "films/%s/edit";

    private final Grid<Film> grid = new Grid<>(Film.class, false);

    // Componentes del formulario
    private TextField filmIdField; // Para mostrar el ID, pero no para editarlo directamente
    private TextField title;
    private TextArea description;
    private IntegerField releaseYear;
    private ComboBox<Language> language;
    private IntegerField rentalDuration;
    private NumberField rentalRate;
    private IntegerField length;
    private NumberField replacementCost;
    private ComboBox<MpaaRating> rating;
    private TextField specialFeatures; // Usaremos un TextField para mostrar/editar la lista de strings separada por comas

    // Botones del formulario
    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");
    // private final Button print = new Button("Imprimir"); // Comentado, si necesitas JasperReports, descomenta

    // Binder para la entidad Film
    private final BeanValidationBinder<Film> binder;

    // Entidad Film que se está editando
    private Film film;

    // Servicios
    private final FilmService filmService;
    private final LanguageService languageService; // Inyectar LanguageService

    @Autowired
    public FilmView(FilmService filmService, LanguageService languageService) {
        this.filmService = filmService;
        this.languageService = languageService; // Asignar LanguageService

        addClassNames("master-detail-screen");
        setSizeFull();

        // Crear la UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configuración de las columnas del Grid
        grid.addColumn("filmId").setAutoWidth(true).setSortable(true);
        grid.addColumn("title").setAutoWidth(true).setSortable(true);
        grid.addColumn("releaseYear").setAutoWidth(true).setSortable(true);
        grid.addColumn("rentalDuration").setAutoWidth(true).setSortable(true);
        grid.addColumn("rentalRate").setAutoWidth(true).setSortable(true);
        grid.addColumn(film -> film.getLanguage() != null ? film.getLanguage().getName() : "N/A")
                .setHeader("Language").setAutoWidth(true).setSortable(true);
        grid.addColumn("rating").setAutoWidth(true).setSortable(true);
        grid.addColumn("lastUpdate").setAutoWidth(true).setSortable(true); // Se muestra la fecha y hora de la última actualización

        // Ocultar otras columnas que no necesitamos mostrar en el Grid o que se manejan en el formulario
        grid.getColumns().forEach(col -> {
            if (col.getKey() != null &&
                    (col.getKey().equals("description") || col.getKey().equals("length") ||
                            col.getKey().equals("replacementCost") || col.getKey().equals("specialFeatures"))) {
                col.setVisible(false);
            }
        });

        // Configurar el DataProvider para paginación
        grid.setItems(query -> {
            int offset = query.getOffset();
            int limit = query.getLimit();

            Sort sort = Sort.unsorted();
            if (!query.getSortOrders().isEmpty()) {
                List<Sort.Order> orders = query.getSortOrders().stream()
                        .map(sortOrder -> sortOrder.getDirection() == com.vaadin.flow.data.provider.SortDirection.ASCENDING ?
                                Sort.Order.asc(sortOrder.getSorted()) : Sort.Order.desc(sortOrder.getSorted()))
                        .collect(Collectors.toList());
                sort = Sort.by(orders);
            }

            Pageable pageable = PageRequest.of(offset / limit, limit, sort);
            return filmService.list(pageable).stream();
        });
        grid.setAllRowsVisible(true); // Puedes ponerlo a false si tienes muchos datos y quieres lazy loading
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        // Manejar la selección de filas del Grid para editar
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                // Navega a la ruta de edición con el ID de la película
                UI.getCurrent().navigate(String.format(FILM_EDIT_ROUTE_TEMPLATE, event.getValue().getFilmId()));
            } else {
                // Si no hay selección, limpia el formulario y navega a la ruta base
                clearForm();
                UI.getCurrent().navigate(FilmView.class);
            }
        });

        // Inicializar el Binder
        binder = new BeanValidationBinder<>(Film.class);

        // Bindings explícitos para cada campo
        binder.forField(filmIdField)
                .withConverter(new Converter<String, Integer>() {
                    @Override
                    public Result<Integer> convertToModel(String presentationValue, ValueContext context) {
                        if (presentationValue == null || presentationValue.trim().isEmpty()) {
                            return Result.ok(null);
                        }
                        try {
                            return Result.ok(Integer.parseInt(presentationValue));
                        } catch (NumberFormatException e) {
                            return Result.error("Debe ser un número válido");
                        }
                    }

                    @Override
                    public String convertToPresentation(Integer modelValue, ValueContext context) {
                        return modelValue != null ? String.valueOf(modelValue) : "";
                    }
                })
                .bind(Film::getFilmId, Film::setFilmId);
        binder.bind(title, Film::getTitle, Film::setTitle);
        binder.bind(description, Film::getDescription, Film::setDescription);
        binder.bind(language, Film::getLanguage, Film::setLanguage);
        binder.bind(rating, Film::getRating, Film::setRating);

        // Bindear campos del formulario a propiedades de la entidad Film
        filmIdField.setReadOnly(true); // El ID no se edita, solo se muestra
        //binder.bindInstanceFields(this); // Esto intentará bindear campos con el mismo nombre

        // Mapeos y conversores personalizados para el Binder
        // Mapeo para Year (IntegerField a Year)
        binder.forField(releaseYear)
                .withConverter(new Converter<Integer, Year>() {
                    @Override
                    public Result<Year> convertToModel(Integer yearValue, ValueContext context) {
                        if (yearValue == null) {
                            return Result.ok(null);
                        }
                        try {
                            return Result.ok(Year.of(yearValue));
                        } catch (Exception e) {
                            return Result.error("Año inválido");
                        }
                    }

                    @Override
                    public Integer convertToPresentation(Year yearValue, ValueContext context) {
                        return yearValue != null ? yearValue.getValue() : null;
                    }
                })
                .bind("releaseYear");

        // Mapeo para BigDecimal (NumberField a BigDecimal)
        binder.forField(rentalRate)
                .withConverter(new Converter<Double, BigDecimal>() {
                    @Override
                    public Result<BigDecimal> convertToModel(Double value, ValueContext context) {
                        return value == null ? Result.ok(null) : Result.ok(BigDecimal.valueOf(value));
                    }

                    @Override
                    public Double convertToPresentation(BigDecimal value, ValueContext context) {
                        return value == null ? null : value.doubleValue();
                    }
                })
                .bind("rentalRate");

        binder.forField(replacementCost)
                .withConverter(new Converter<Double, BigDecimal>() {
                    @Override
                    public Result<BigDecimal> convertToModel(Double value, ValueContext context) {
                        return value == null ? Result.ok(null) : Result.ok(BigDecimal.valueOf(value));
                    }

                    @Override
                    public Double convertToPresentation(BigDecimal value, ValueContext context) {
                        return value == null ? null : value.doubleValue();
                    }
                })
                .bind("replacementCost");

        // Mapeo para Short (IntegerField a Short)
        binder.forField(rentalDuration)
                .withConverter(new Converter<Integer, Short>() {
                    @Override
                    public Result<Short> convertToModel(Integer value, ValueContext context) {
                        return value == null ? Result.ok(null) : Result.ok(value.shortValue());
                    }

                    @Override
                    public Integer convertToPresentation(Short value, ValueContext context) {
                        return value == null ? null : value.intValue();
                    }
                })
                .bind("rentalDuration");

        binder.forField(length)
                .withConverter(new Converter<Integer, Short>() {
                    @Override
                    public Result<Short> convertToModel(Integer value, ValueContext context) {
                        return value == null ? Result.ok(null) : Result.ok(value.shortValue());
                    }

                    @Override
                    public Integer convertToPresentation(Short value, ValueContext context) {
                        return value == null ? null : value.intValue();
                    }
                })
                .bind("length");

        // Mapeo para List<String> (TextField a List<String>)
        binder.forField(specialFeatures)
                .withConverter(new Converter<String, List<String>>() {
                    @Override
                    public Result<List<String>> convertToModel(String value, ValueContext context) {
                        if (value == null || value.trim().isEmpty()) {
                            return Result.ok(null);
                        }
                        return Result.ok(Arrays.stream(value.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList()));
                    }

                    @Override
                    public String convertToPresentation(List<String> value, ValueContext context) {
                        return value != null ? String.join(", ", value) : "";
                    }
                })
                .bind("specialFeatures");


        // Listeners para los botones
        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.film == null) {
                    this.film = new Film();
                }
                binder.writeBean(this.film); // Escribe los valores del formulario al objeto Film
                filmService.saveFilm(this.film); // Guarda la película
                clearForm();
                refreshGrid();
                Notification.show("Datos de película actualizados correctamente.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate(FilmView.class); // Navega de vuelta a la vista de lista
            } catch (ObjectOptimisticLockingFailureException exception) {
                // Error de concurrencia
                Notification n = Notification.show(
                        "Error al actualizar los datos. Alguien más ha actualizado el registro mientras realizabas cambios.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                // Errores de validación del BeanValidationBinder
                Notification.show("La actualización de datos falló. Verificar que todos los valores son válidos.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // Comentado JasperReports, descomentar si es necesario
        /*
        print.addClickListener(e -> {
            try {
                // ... lógica de JasperReports aquí ...
                Notification.show("Reporte PDF generado y descargado.", 3000, Notification.Position.MIDDLE);
            } catch (Exception exception) {
                exception.printStackTrace();
                Notification.show("Error al generar el reporte " + exception.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        */
    }

    // Método llamado antes de entrar a la vista para manejar parámetros de ruta
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> filmId = event.getRouteParameters().get(FILM_ID).map(Integer::parseInt);
        if (filmId.isPresent()) {
            Optional<Film> filmFromBackend = filmService.getFilmById(filmId.get());
            if (filmFromBackend.isPresent()) {
                populateForm(filmFromBackend.get());
            } else {
                Notification.show(
                        String.format("La película requerida no se encontró, ID = %s", filmId.get()), 3000,
                        Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
                refreshGrid();
                event.forwardTo(FilmView.class); // Redirige a la vista principal si no se encuentra
            }
        }
    }

    // Diseño del editor (formulario)
    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        // Inicializar los campos del formulario
        filmIdField = new TextField("Film ID");
        title = new TextField("Title");
        description = new TextArea("Description");
        releaseYear = new IntegerField("Release Year");
        language = new ComboBox<>("Language");
        language.setItems(languageService.findAll()); // Carga todos los lenguajes disponibles
        language.setItemLabelGenerator(Language::getName); // Muestra el nombre del lenguaje
        language.setClearButtonVisible(true); // Permite deseleccionar si no es obligatorio
        rentalDuration = new IntegerField("Rental Duration (days)");
        rentalRate = new NumberField("Rental Rate");
        length = new IntegerField("Length (minutes)");
        replacementCost = new NumberField("Replacement Cost");
        rating = new ComboBox<>("Rating");
        rating.setItems(MpaaRating.values()); // Carga todos los valores del enum MpaaRating
        rating.setClearButtonVisible(true);
        specialFeatures = new TextField("Special Features (comma-separated)");
        specialFeatures.setPlaceholder("e.g., Trailers,Commentaries");

        formLayout.add(filmIdField, title, description, releaseYear, language,
                rentalDuration, rentalRate, length, replacementCost, rating, specialFeatures);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    // Diseño de los botones del formulario
    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // print.addThemeVariants(ButtonVariant.LUMO_CONTRAST); // Comentado
        buttonLayout.add(save, cancel); // Quitar print si está comentado
        editorLayoutDiv.add(buttonLayout);
    }

    // Diseño del Grid
    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull(); // Importante para que el grid ocupe todo el espacio
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    // Refresca el grid
    private void refreshGrid() {
        grid.select(null); // Deselecciona cualquier fila
        grid.getDataProvider().refreshAll(); // Fuerza la recarga de datos
    }

    // Limpia el formulario
    private void clearForm() {
        populateForm(null); // Establece el objeto film a null y resetea el binder
    }

    // Popula el formulario con los datos de una película (o lo limpia si es null)
    private void populateForm(Film value) {
        this.film = value;
        binder.readBean(this.film); // Lee el bean y llena el formulario
        // Si es una nueva película, el filmIdField debe estar vacío
        if (this.film == null || this.film.getFilmId() == null) {
            filmIdField.setValue("");
        } else {
            filmIdField.setValue(this.film.getFilmId().toString());
        }
    }
}