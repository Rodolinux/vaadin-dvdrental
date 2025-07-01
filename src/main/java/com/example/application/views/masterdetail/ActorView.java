package com.example.application.views.masterdetail;

import com.example.application.data.entity.Actor;
import com.example.application.data.entity.City;
import com.example.application.data.reportbean.ActorReportBean;
import com.example.application.data.reportbean.CityReportBean;
import com.example.application.helpers.PrintReportHelper;
import com.example.application.services.ActorService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("CRUD de actores")
@Route(value = ":actorId?/:action?(edit)")
@Menu(order = 0, icon = "las la-columns")
@RouteAlias("")
@Uses(Icon.class)
public class ActorView extends Div implements BeforeEnterObserver {

    private final String ACTOR_ID = "actorId";
    private final String ACTOR_EDIT_ROUTE_TEMPLATE = "/%s/edit";

    private final Grid<Actor> grid = new Grid<>(Actor.class, false);
    private TextField actorId;
    private TextField firstName;
    private TextField lastName;

    private final Button newActor = new Button("Nuevo");
    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");
    private final Button print = new Button("Imprimir");

    private final BeanValidationBinder<Actor> binder;

    private Actor actor;

    private final ActorService actorService;

    public ActorView(ActorService actorService) {
        this.actorService = actorService;
        addClassNames("master-detail-screen");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);


        grid.addColumn("actorId").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("firstName").setAutoWidth(true).setSortable(true).setResizable(true);
        grid.addColumn("lastName").setAutoWidth(true).setSortable(true).setResizable(true);

        grid.setItems(query -> actorService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);


        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ACTOR_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ActorView.class);
            }
        });


        binder = new BeanValidationBinder<>(Actor.class);


        binder.bindInstanceFields(this);


        newActor.addClickListener(event -> {
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(ActorView.class);

        });
        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.actor == null) {
                    this.actor = new Actor();
                }
                binder.writeBean(this.actor);
                actorService.save(this.actor);
                clearForm();
                refreshGrid();
                Notification.show("Datos actualizados");
                UI.getCurrent().navigate(ActorView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error al actualizar los datos. Alguien más ha actualizado el registro mientras realizabas cambios.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("La actualización de datos falló. Verificar que la actualización de todos los valores es válida");
            }
        });

        print.addClickListener(e -> {
            List<Actor> actors = actorService.list(Pageable.unpaged()).getContent();
            List<ActorReportBean> beans = actors.stream().
                    map(ActorReportBean::new)
                    .collect(Collectors.toList());

            Map<String, Object> params = new HashMap<>();
            params.put("Creado por", "Vaadin DVDRental App");

            PrintReportHelper.generateAndDownloadReport(
                    "/reports/actor_report.jrxml",
                    beans,
                    params,
                    "actor_report"
            );


            /*
            try {
                //1. Cargo el reporte desde la ruta especificada
                InputStream jrxmlStream = getClass().getResourceAsStream("/reports/actor_report.jrxml");
                if (jrxmlStream == null) {
                    throw new RuntimeException("No encontré el reporte en  /reports/actor_report.jrxml. Make sure it's compiled or the path is correct.");
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
                List<Actor> actors = actorService.listAll();
                List<ActorReportBean> reportBeans = actors.stream()
                        .map(ActorReportBean::new)
                        .collect(Collectors.toList());
                System.out.println("Actores para el reporte " + reportBeans.size());

                //4. Establezo el datasource que voy a compilar y cargo un par de parámetros
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

                try (FileOutputStream fos = new FileOutputStream("actor_report_debug.pdf")) {
                    pdfOutputStream.writeTo(fos);
                    System.out.println("PDF grabado a  actor_report_debug.pdf.");
                } catch (IOException ex) {
                    System.err.println("Error grabando debug PDF: " + ex.getMessage());
                }

                //7. Mando el PDF al browser. Con CallJsFunction hago que vaadin cliquee en un enlace que se crea para descargar el archivo
                String reportFileName = "actor_report_" + System.currentTimeMillis() + ".pdf";
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
            */
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> actorId = event.getRouteParameters().get(ACTOR_ID).map(Long::parseLong);
        if (actorId.isPresent()) {
            Optional<Actor> actorFromBackend = actorService.get(actorId.get());
            if (actorFromBackend.isPresent()) {
                populateForm(actorFromBackend.get());
            } else {
                Notification.show(
                        String.format("El actor requerido no se encontró, ID = %s", actorId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(ActorView.class);
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
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        formLayout.add(firstName, lastName);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        newActor.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        print.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(newActor,save, cancel, print);
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

    private void populateForm(Actor value) {
        this.actor = value;
        binder.readBean(this.actor);
    }
}
