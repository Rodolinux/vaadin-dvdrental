package com.example.application.views.masterdetail;

import com.example.application.data.entity.Address;
import com.example.application.data.entity.Staff;
import com.example.application.data.entity.Store;
import com.example.application.services.AddressService;
import com.example.application.services.StaffService;
import com.example.application.services.StoreService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("CRUD de Personal")
@Route(value = "staff/:staffId?/:action?(edit)")
@Menu(order = 3, icon = "la la-user") // Orden 3 para aparecer después de Direcciones
@RouteAlias("staff")
@Uses(Icon.class)
public class StaffView extends Div implements BeforeEnterObserver {

    private final String STAFF_ID = "staffId";
    private final String STAFF_EDIT_ROUTE_TEMPLATE = "staff/%s/edit";

    private final Grid<Staff> grid = new Grid<>(Staff.class, false);

    // Componentes del formulario
    private TextField staffIdField;
    private TextField firstName;
    private TextField lastName;
    private ComboBox<Address> address; // ComboBox para seleccionar la Dirección
    private TextField email;
    private ComboBox<Store> store; // ComboBox para seleccionar la Tienda
    private Checkbox active;
    private TextField username;
    private PasswordField password; // Para la contraseña
    private DateTimePicker lastUpdate;

    // Botones del formulario
    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");
    private final Button newStaff = new Button("Nuevo Personal");

    // Binder para la entidad Staff
    private final BeanValidationBinder<Staff> binder;

    // Entidad Staff que se está editando
    private Staff staff;

    // Servicios
    private final StaffService staffService;
    private final AddressService addressService; // Necesitamos AddressService para poblar el ComboBox de direcciones
    private final StoreService storeService; // Necesitamos StoreService para poblar el ComboBox de tiendas

    @Autowired
    public StaffView(StaffService staffService, AddressService addressService, StoreService storeService) {
        this.staffService = staffService;
        this.addressService = addressService;
        this.storeService = storeService;

        addClassNames("master-detail-screen");
        setSizeFull();

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configuración de las columnas del Grid
        grid.addColumn("staffId").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("firstName").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("lastName").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn(staff -> staff.getAddress() != null ?
                        staff.getAddress().getAddress1() + " (" + staff.getAddress().getCityId().getCityName() + ")" : "N/A")
                .setHeader("Dirección (Ciudad)").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("email").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn(staff -> staff.getStore() != null ? staff.getStore().getStoreId() : "N/A")
                .setHeader("Tienda ID").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("active").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("username").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("lastUpdate").setAutoWidth(true).setSortable(true).setResizable(true);

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
            return staffService.list(pageable).stream();
        });
        grid.setAllRowsVisible(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(STAFF_EDIT_ROUTE_TEMPLATE, event.getValue().getStaffId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(StaffView.class);
            }
        });

        binder = new BeanValidationBinder<>(Staff.class);

        // Binding para staffIdField (TextField a Integer) - solo lectura
        binder.forField(staffIdField)
                .withConverter(new StringToIntegerConverter()) // Clase de conversor reutilizada
                .bind(Staff::getStaffId, Staff::setStaffId);

        // Bindings explícitos para los otros campos
        binder.bind(firstName, Staff::getFirstName, Staff::setFirstName);
        binder.bind(lastName, Staff::getLastName, Staff::setLastName);
        binder.bind(address, Staff::getAddress, Staff::setAddress); // Binding directo para el objeto Address
        binder.bind(email, Staff::getEmail, Staff::setEmail);
        binder.bind(store, Staff::getStore, Staff::setStore); // Binding directo para el objeto Store
        binder.bind(active, Staff::getActive, Staff::setActive);
        binder.bind(username, Staff::getUsername, Staff::setUsername);
        binder.bind(password, Staff::getPassword, Staff::setPassword); // Cuidado con la seguridad en producción
        binder.bind(lastUpdate, Staff::getLastUpdate, Staff::setLastUpdate);

        // Listeners para los botones
        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.staff == null) {
                    this.staff = new Staff();
                }
                binder.writeBean(this.staff);
                staffService.saveStaff(this.staff);
                clearForm();
                refreshGrid();
                Notification.show("Datos de personal actualizados correctamente.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate(StaffView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error al actualizar los datos. Alguien más ha actualizado el registro mientras realizabas cambios.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("La actualización de datos falló. Verificar que todos los valores son válidos.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        newStaff.addClickListener(e -> {
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(StaffView.class);
        });
    }

    // Clase estática anidada para la conversión de String a Integer (reutilizada)
    private static class StringToIntegerConverter implements Converter<String, Integer> {
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
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> staffId = event.getRouteParameters().get(STAFF_ID).map(Integer::parseInt);
        if (staffId.isPresent()) {
            Optional<Staff> staffFromBackend = staffService.getStaffById(staffId.get());
            if (staffFromBackend.isPresent()) {
                populateForm(staffFromBackend.get());
            } else {
                Notification.show(
                        String.format("El personal requerido no se encontró, ID = %s", staffId.get()), 3000,
                        Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
                refreshGrid();
                event.forwardTo(StaffView.class);
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
        staffIdField = new TextField("Staff ID");
        staffIdField.setReadOnly(true);

        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");

        address = new ComboBox<>("Address");
        // Carga las primeras 1000 direcciones. Considera Lazy-loading para datasets grandes.
        address.setItems(addressService.list(PageRequest.of(0, 1000)).getContent());
        address.setItemLabelGenerator(a -> a.getAddress1() + " (" + a.getCityId().getCityName() + ")");
        address.setClearButtonVisible(true);

        email = new TextField("Email (Optional)");

        store = new ComboBox<>("Store");
        store.setItems(storeService.findAll()); // Carga todas las tiendas
        store.setItemLabelGenerator(s -> s.getStoreId().toString()); // Muestra el ID de la tienda
        store.setClearButtonVisible(true);

        active = new Checkbox("Active");
        username = new TextField("Username");
        password = new PasswordField("Password"); // ¡Importante: En producción, nunca manejes contraseñas en texto plano!

        lastUpdate = new DateTimePicker("Last Update");
        lastUpdate.setReadOnly(true);

        formLayout.add(staffIdField, firstName, lastName, address, email, store, active, username, password, lastUpdate);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        newStaff.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(newStaff, save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();
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

    private void populateForm(Staff value) {
        this.staff = value;
        binder.readBean(this.staff);
        if (this.staff == null || this.staff.getStaffId() == null) {
            staffIdField.setValue("");
        }
        if (value == null) {
            lastUpdate.setValue(null);
            active.setValue(false); // Por defecto, nuevo personal inactivo
            password.setValue(""); // Limpiar contraseña al crear nuevo
        }
    }
}
