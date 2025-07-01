package com.example.application.helpers;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.StreamResource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class PrintReportHelper {
    public static <T> void generateAndDownloadReport(String reportPath,
                                                List<T> dataList,
                                                Map<String,Object> parameters,
                                                String fileNameWithoutExt) {
        try {
            // 1. Cargar JRXML
            InputStream jrxmlStream = PrintReportHelper.class.getResourceAsStream(reportPath);
            if (jrxmlStream == null) {
                throw new RuntimeException("No encontré el archivo del reporte en: " + reportPath);
            }

            System.out.println("Compilando reporte...");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            System.out.println("Reporte compilado.");

            // 2. Datasource
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

            // 3. Llenar reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            System.out.println("Reporte llenado.");

            // 4. Exportar a PDF en memoria
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, pdfOutputStream);
            System.out.println("PDF generado: " + pdfOutputStream.size() + " bytes.");

            // 5. Crear recurso y disparar descarga en el browser
            String reportFileName = fileNameWithoutExt + "_" + System.currentTimeMillis() + ".pdf";
            StreamResource resource = new StreamResource(reportFileName, () -> new ByteArrayInputStream(pdfOutputStream.toByteArray()));
            resource.setContentType("application/pdf");

            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            UI.getCurrent().getElement().appendChild(downloadLink.getElement());
            downloadLink.getElement().callJsFunction("click");
            UI.getCurrent().getPage().executeJs("setTimeout(() => arguments[0].remove(), 1000)", downloadLink.getElement());

            Notification.show("Reporte PDF generado y descargado.", 3000, Notification.Position.MIDDLE);

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error generando reporte: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
}
