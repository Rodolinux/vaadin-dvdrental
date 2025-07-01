package com.example.application.views.masterdetail;

import com.example.application.data.entity.Address;
import com.example.application.data.entity.City;
import com.example.application.data.reportbean.AddressReportBean;
import com.example.application.data.reportbean.CityReportBean;
import com.example.application.services.AddressService;
import com.example.application.services.CityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
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
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("CRUD de direcciones")
@Route(value = "addresses/:addressId?/:action?(edit)")
@Menu(order = 2, icon="la la-map-marker")
@RouteAlias("addresses")
@Uses(Icon.class)


public class AddressView extends Div implements BeforeEnterObserver {

    private final String ADDRESS_ID = "addressId";
    private final String ADDRESS_EDIT_ROUTE_TEMPLATE = "addresses/%s/edit";

    private final Grid<Address> grid = new Grid<>(Address.class, false);

    // Componentes del formulario
    private TextField addressIdField;
    private TextField address1;
    private TextField address2;
    private TextField district;
    private ComboBox<City> city; // ComboBox para seleccionar la Ciudad
    private TextField postalCode;
    private TextField phone;
    private DateTimePicker lastUpdate;

    // Botones del formulario
    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");
    private final Button newAddress = new Button("Nueva Dirección");
    private final Button print = new Button("Imprimir");

    // Binder para la entidad Address
    private final BeanValidationBinder<Address> binder;

    // Entidad Address que se está editando
    private Address address;

    // Servicios
    private final AddressService addressService;
    private final CityService cityService; // Necesitamos CityService para poblar el ComboBox

    @Autowired
    public AddressView(AddressService addressService, CityService cityService) {
        this.addressService = addressService;
        this.cityService = cityService;

        addClassNames("master-detail-screen");
        setSizeFull();

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configuración de las columnas del Grid
        grid.addColumn("addressId").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("address1").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("address2").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("district").setAutoWidth(true).setSortable(true).setResizable(true);
        // Mostrar el nombre de la ciudad (y opcionalmente el país)
        grid.addColumn(address -> address.getCityId() != null ?
                        address.getCityId().getCityName() + " (" + address.getCityId().getCountry().getCountryName() + ")" : "N/A")
                .setHeader("Ciudad (País)").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("postalCode").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("phone").setAutoWidth(true).setSortable(true).setResizable(true);
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
            return addressService.list(pageable).stream();
        });
        grid.setAllRowsVisible(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ADDRESS_EDIT_ROUTE_TEMPLATE, event.getValue().getAddressId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AddressView.class);
            }
        });

        binder = new BeanValidationBinder<>(Address.class);

        // Binding para addressIdField (TextField a Integer) - solo lectura
        binder.forField(addressIdField)
                .withConverter(new StringToIntegerConverter()) // Clase de conversor reutilizada
                .bind(Address::getAddressId, Address::setAddressId);

        // Bindings explícitos para los otros campos
        binder.bind(address1, Address::getAddress1, Address::setAddress1);
        binder.bind(address2, Address::getAddress2, Address::setAddress2);
        binder.bind(district, Address::getDistrict, Address::setDistrict);
        binder.bind(city, Address::getCityId, Address::setCityId); // Binding directo para el objeto City
        binder.bind(postalCode, Address::getPostalCode, Address::setPostalCode);
        binder.bind(phone, Address::getPhone, Address::setPhone);
        binder.bind(lastUpdate, Address::getLastUpdate, Address::setLastUpdate);

        // Listeners para los botones
        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.address == null) {
                    this.address = new Address();
                }
                binder.writeBean(this.address);
                addressService.saveAddress(this.address);
                clearForm();
                refreshGrid();
                Notification.show("Datos de dirección actualizados correctamente.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate(AddressView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error al actualizar los datos. Alguien más ha actualizado el registro mientras realizabas cambios.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("La actualización de datos falló. Verificar que todos los valores son válidos.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        newAddress.addClickListener(e -> {
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(AddressView.class);
        });

        print.addClickListener(e -> {
            try {
                //1. Cargo el reporte desde la ruta especificada
                InputStream jrxmlStream = getClass().getResourceAsStream("/reports/address_report.jrxml");
                if (jrxmlStream == null) {
                    throw new RuntimeException("No encontré el reporte en  /reports/address_report.jrxml.");
                }

                //2. Compilar el reporte. Recordar que este paso se puede evitar precompilando el reporte como .jasper en Jasper Studio
                System.out.println("Cargando archivo JRXML ...");
                System.out.println("Compilar reporte...");
                JasperReport jasperReport;
                try {
                    jasperReport = JasperCompileManager.compileReport(jrxmlStream);
                    System.out.println("Reporte compilado exitosamente.");
                } catch (Exception compileEx) {
                    System.err.println("Error de compilación : " + compileEx.getMessage());
                    throw new RuntimeException("Compilación de reporte fallida", compileEx);
                }
                //3. Hago un wrapping a la clase Actor para resolver un detalle con las fechas con el que jasper se pone muy quisquilloso
                List<Address> addresses = addressService.list(Pageable.unpaged()).getContent();
                List<AddressReportBean> reportBeans = addresses.stream()
                        .map(AddressReportBean::new)
                        .collect(Collectors.toList());
                System.out.println("Direcciones para el reporte " + reportBeans.size());

                //4. Establezco el datasource que voy a compilar y cargo un par de parámetros
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportBeans);
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("Creado por", "Vaadin DVDRental App");

                //5. Lleno el reporte usando el archivo que cargué + datasource+ parámetros
                System.out.println("Llenando el reporte...");
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                System.out.println("Reporte llenado exitosamente.");

                //6. Genero una salida binaria para exportar el PDF
                System.out.println("Exportando a PDF...");
                ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                JasperExportManager.exportReportToPdfStream(jasperPrint, pdfOutputStream);
                System.out.println("Tamaño del PDF: " + pdfOutputStream.size() + " bytes.");

                try (FileOutputStream fos = new FileOutputStream("address_report_debug.pdf")) {
                    pdfOutputStream.writeTo(fos);
                    System.out.println("PDF grabado a  address_report_debug.pdf.");
                } catch (IOException ex) {
                    System.err.println("Error grabando debug PDF: " + ex.getMessage());
                }

                //7. Mando el PDF al browser. Con CallJsFunction hago que vaadin cliquee en un enlace que se crea para descargar el archivo
                String reportFileName = "address_report_" + System.currentTimeMillis() + ".pdf";
                StreamResource resource = new StreamResource(reportFileName, () -> new ByteArrayInputStream(pdfOutputStream.toByteArray()));
                resource.setContentType("application/pdf");
                Anchor downloadLink = new Anchor(resource, "");
                add(downloadLink);
                downloadLink.getElement().callJsFunction("click");
                UI.getCurrent().getPage().executeJs(
                        "setTimeout(() => arguments[0].remove(), 1000)", downloadLink.getElement());

                Notification.show("Reporte PDF generado y descargado.", 3000, Notification.Position.MIDDLE);

            } catch (Exception exception) {
                exception.printStackTrace();
                Notification.show("Error al generar el reporte " + exception.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
    }

    // Clase estática anidada para la conversión de String a Integer (reutilizada de CityView)
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
        Optional<Integer> addressId = event.getRouteParameters().get(ADDRESS_ID).map(Integer::parseInt);
        if (addressId.isPresent()) {
            Optional<Address> addressFromBackend = addressService.getAddressById(addressId.get());
            if (addressFromBackend.isPresent()) {
                populateForm(addressFromBackend.get());
            } else {
                Notification.show(
                        String.format("La dirección requerida no se encontró, ID = %s", addressId.get()), 3000,
                        Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
                refreshGrid();
                event.forwardTo(AddressView.class);
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
        addressIdField = new TextField("Address ID");
        addressIdField.setReadOnly(true);

        address1 = new TextField("Address Line 1");
        address2 = new TextField("Address Line 2 (Optional)");
        district = new TextField("District");

        city = new ComboBox<>("City");
        // Nota: Si hay muchas ciudades, considera un Lazy-loading para este ComboBox
        city.setItems(cityService.list(PageRequest.of(0, 1000)).getContent()); // Carga las primeras 1000 ciudades
        city.setItemLabelGenerator(c -> c.getCityName() + " (" + (c.getCountry() != null ? c.getCountry().getCountryName() : "N/A") + ")");
        city.setClearButtonVisible(true);

        postalCode = new TextField("Postal Code (Optional)");
        phone = new TextField("Phone");

        lastUpdate = new DateTimePicker("Last Update");
        lastUpdate.setReadOnly(true);

        formLayout.add(addressIdField, address1, address2, district, city, postalCode, phone, lastUpdate);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        newAddress.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        print.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(newAddress, save, print, cancel);
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

    private void populateForm(Address value) {
        this.address = value;
        binder.readBean(this.address);
        if (this.address == null || this.address.getAddressId() == null) {
            addressIdField.setValue("");
        }
        if (value == null) {
            lastUpdate.setValue(null);
        }
    }
}
