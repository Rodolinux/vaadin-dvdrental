package com.example.application.views.masterdetail;

import com.example.application.data.City;
import com.example.application.data.Country;
import com.example.application.services.CityService;
import com.example.application.services.CountryService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("CRUD de Ciudades")
@Route(value="cities/:cityId?/:action?(edit)")
@Menu(order = 1, icon = "la la-city")
@RouteAlias("cities")
@Uses(Icon.class)
public class CityView extends Div implements BeforeEnterObserver {
    private final String CITY_ID = "cityId";
    private final String CITY_EDIT_ROUTE_TEMPLATE = "cities/%s/edit";

    private final Grid<City> grid =  new Grid<>(City.class, false);

    private TextField cityIdField;
    private TextField cityName;
    private ComboBox<Country> country;
    private DateTimePicker lastUpdate;

    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");
    private final Button newCity = new Button("Nueva");

    private final BeanValidationBinder<City> binder;

    private City city;

    private final CityService cityService;

    private final CountryService countryService;

    @Autowired
    public CityView(CityService cityService, CountryService countryService) {
        this.cityService = cityService;
        this.countryService = countryService;
        addClassNames("master-detail-screen");
        setSizeFull();

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.addColumn("cityId").setAutoWidth(true).setSortable(true);
        grid.addColumn("cityName").setAutoWidth(true).setSortable(true);
        grid.addColumn(city -> city.getCountry() != null ? city.getCountry().getCountryName(): "N/A")
                .setHeader("Country").setAutoWidth(true).setSortable(true);
        grid.addColumn("lastUpdate").setAutoWidth(true).setSortable(true);

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

            PageRequest pageable = PageRequest.of(offset / limit, limit, sort);
            return cityService.findAll(pageable).stream();
        });

        grid.setAllRowsVisible(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                UI.getCurrent().navigate(String.format(CITY_EDIT_ROUTE_TEMPLATE, e.getValue().getCityId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CityView.class);
            }
        });
        binder = new BeanValidationBinder<>(City.class);

        binder.forField(cityIdField)
                .withConverter(new Converter<String, Integer>() {
                    @Override
                    public Result<Integer> convertToModel(String presentationValue, ValueContext context){
                        if (presentationValue == null || presentationValue.trim().isEmpty()) {
                            return Result.ok(null);
                        }
                        try{
                            return Result.ok(Integer.parseInt(presentationValue));
                        } catch (NumberFormatException e){
                            return Result.error("Debe ser un número válido");
                        }
                    }
                    @Override
                    public String convertToPresentation(Integer modelValue, ValueContext context){
                        return modelValue == null ? "" : String.valueOf(modelValue);

                    }
                })
                .bind(City::getCityId, City::setCityId);

       //binder.bind(cityIdField,City::getCityId, City::setCityId);
        binder.bind(cityName,City::getCityName, City::setCityName);
        binder.bind(country,City::getCountry, City::setCountry);
        binder.bind(lastUpdate,City::getLastUpdate, City::setLastUpdate);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try{
                if (this.city == null){
                    this.city = new City();
                }
                binder.writeBean(this.city);
                cityService.save(this.city);
                clearForm();
                refreshGrid();
                Notification.show("Datos de ciudad actualizados correctamente.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate(CityView.class);

            } catch (ObjectOptimisticLockingFailureException exception){
                Notification n =  Notification.show(
                        "Error al actualizar los datos. Alguien más ha actualizado el registro mientras realizabas cambios.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException v){
                Notification.show("La actualización de datos falló. Verificar que todos los valores son válidos.").addThemeVariants(NotificationVariant.LUMO_ERROR);

            }


        });
        newCity.addClickListener(e -> {
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(CityView.class);
        });

    }



    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> cityId = event.getRouteParameters().get(CITY_ID).map(Integer::parseInt);
        if(cityId.isPresent()){
            Optional<City> cityFromBackend = cityService.findById(cityId.get());
            if(cityFromBackend.isPresent()){
                populateForm(cityFromBackend.get());
            } else {
                Notification.show(
                        String.format("La ciudad requerida no se encontró, ID = %s", cityId.get()), 3000,
                        Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
                refreshGrid();
                event.forwardTo(CityView.class);
            }
        }
    }

    public void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        cityIdField = new TextField("City ID");
        cityIdField.setReadOnly(true);

        cityName = new TextField("City name");
        country = new ComboBox<>("Country");

        country.setItems(countryService.findAll());
        country.setItemLabelGenerator(Country::getCountryName);
        country.setClearButtonVisible(true);

        lastUpdate = new DateTimePicker("Last Update");
        lastUpdate.setReadOnly(true);

        formLayout.add(cityIdField, cityName, country, lastUpdate);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        newCity.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(newCity, save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private  void refreshGrid(){
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(City value) {
        this.city = value;
        binder.readBean(this.city);
        if(this.city == null || this.city.getCityId() == null){
            cityIdField.setValue("");

        }
        if(value == null){
            lastUpdate.setValue(null);
        }
    }


}
