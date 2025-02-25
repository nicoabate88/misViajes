package abate.abate.servicios;

import abate.abate.entidades.Camion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class ExcelServicio {

    public void exportHtmlToExcel(String htmlContent, HttpServletResponse response) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Viajes");

        int rowIndex = 0;

        sheet.createRow(rowIndex++);

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        // Intenta convertir el texto en un número
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        // Si no es un número, se guarda como texto
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = 0;
        if (sheet.getRow(3) != null) {
            columnCount = sheet.getRow(3).getPhysicalNumberOfCells();
        }

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Viajes.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportHtmlToExcelCombustible(String htmlContent, HttpServletResponse response, Double consumo) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Combustible");

        // Crear estilos para el título y el subtítulo
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        titleStyle.setFont(titleFont);

        int rowIndex = 0;

        // Escribir el título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Consumo " + consumo + " L / 100 Km");
        titleCell.setCellStyle(titleStyle);

        sheet.createRow(rowIndex++);

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        // Intenta convertir el texto en un número
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        // Si no es un número, se guarda como texto
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = 0;
        if (sheet.getRow(3) != null) {
            columnCount = sheet.getRow(3).getPhysicalNumberOfCells();
        }

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Combustible.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportHtmlToExcelEstadistica(String htmlContent, HttpServletResponse response, Camion camion) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Estadisticas");

        // Crear estilos para el título y el subtítulo
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        titleStyle.setFont(titleFont);

        int rowIndex = 0;

        // Escribir el título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(camion.getDominio() + ' ' + camion.getMarca() + ' ' + camion.getModelo());
        titleCell.setCellStyle(titleStyle);

        sheet.createRow(rowIndex++);

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        // Intenta convertir el texto en un número
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        // Si no es un número, se guarda como texto
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = 0;
        if (sheet.getRow(3) != null) {
            columnCount = sheet.getRow(3).getPhysicalNumberOfCells();
        }

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Estadisticas.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportHtmlToExcelGasto(String htmlContent, HttpServletResponse response, Camion camion) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Gastos");

        // Crear estilos para el título y el subtítulo
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        titleStyle.setFont(titleFont);

        int rowIndex = 0;

        // Escribir el título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(camion.getDominio() + ' ' + camion.getMarca() + ' ' + camion.getModelo());
        titleCell.setCellStyle(titleStyle);

        sheet.createRow(rowIndex++);

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        // Intenta convertir el texto en un número
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        // Si no es un número, se guarda como texto
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = 0;
        if (sheet.getRow(3) != null) {
            columnCount = sheet.getRow(3).getPhysicalNumberOfCells();
        }

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Gastos.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportHtmlToExcelCuenta(String htmlContent, HttpServletResponse response, String nombre, Double saldo) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Cuenta");

        // Crear estilos para el título y el subtítulo
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        titleStyle.setFont(titleFont);

        int rowIndex = 0;

        // Escribir el título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(nombre);
        titleCell.setCellStyle(titleStyle);

        Row subtitleRow = sheet.createRow(rowIndex++);
        Cell subtitleCell = subtitleRow.createCell(0);
        subtitleCell.setCellValue("Saldo: " + saldo);
        subtitleCell.setCellStyle(titleStyle);

        sheet.createRow(rowIndex++);

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        // Intenta convertir el texto en un número
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        // Si no es un número, se guarda como texto
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = 0;
        if (sheet.getRow(3) != null) {
            columnCount = sheet.getRow(3).getPhysicalNumberOfCells();
        }

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Cuenta.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportHtmlToExcelCaja(String htmlContent, HttpServletResponse response, String nombre, Double saldo) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Caja");

        // Crear estilos para el título y el subtítulo
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        titleStyle.setFont(titleFont);

        int rowIndex = 0;

        // Escribir el título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(nombre);
        titleCell.setCellStyle(titleStyle);

        Row subtitleRow = sheet.createRow(rowIndex++);
        Cell subtitleCell = subtitleRow.createCell(0);
        subtitleCell.setCellValue("Saldo: " + saldo);
        subtitleCell.setCellStyle(titleStyle);

        sheet.createRow(rowIndex++);

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        // Intenta convertir el texto en un número
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        // Si no es un número, se guarda como texto
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = 0;
        if (sheet.getRow(3) != null) {
            columnCount = sheet.getRow(3).getPhysicalNumberOfCells();
        }

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Caja.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportHtmlToExcelCuentaMovimiento(String htmlContent, HttpServletResponse response, String nombre, String desde, String hasta) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("MovimientosCuenta");

        // Crear estilos para el título y el subtítulo
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        titleStyle.setFont(titleFont);

        int rowIndex = 0;

        // Escribir el título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(nombre);
        titleCell.setCellStyle(titleStyle);

        Row subtitleRow = sheet.createRow(rowIndex++);
        Cell subtitleCell = subtitleRow.createCell(0);
        subtitleCell.setCellValue("Movimientos entre: " + desde + " y " + hasta);
        subtitleCell.setCellStyle(titleStyle);

        sheet.createRow(rowIndex++);

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        // Intenta convertir el texto en un número
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        // Si no es un número, se guarda como texto
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = 0;
        if (sheet.getRow(3) != null) {
            columnCount = sheet.getRow(3).getPhysicalNumberOfCells();
        }

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MovimientosCuenta.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportHtmlToExcelCajaMovimiento(String htmlContent, HttpServletResponse response, String nombre, String desde, String hasta) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("MovimientosCaja");

        // Crear estilos para el título y el subtítulo
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        titleStyle.setFont(titleFont);

        int rowIndex = 0;

        // Escribir el título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(nombre);
        titleCell.setCellStyle(titleStyle);

        Row subtitleRow = sheet.createRow(rowIndex++);
        Cell subtitleCell = subtitleRow.createCell(0);
        subtitleCell.setCellValue("Movimientos entre: " + desde + " y " + hasta);
        subtitleCell.setCellStyle(titleStyle);

        sheet.createRow(rowIndex++);

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        // Intenta convertir el texto en un número
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        // Si no es un número, se guarda como texto
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = 0;
        if (sheet.getRow(3) != null) {
            columnCount = sheet.getRow(3).getPhysicalNumberOfCells();
        }

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MovimientosCaja.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportHtmlToExcelEstadisticaCamiones(String htmlContent, HttpServletResponse response) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("EstadisticasCamiones");

        int rowIndex = 0;

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=EstadisticasCamiones.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportHtmlToExcelEstadisticaChoferes(String htmlContent, HttpServletResponse response) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("EstadisticasChoferes");

        int rowIndex = 0;

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=EstadisticasChoferes.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

}
   



