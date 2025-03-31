package co.com.certification.practiceautomatedtesting.utils.various;

import co.com.certification.practiceautomatedtesting.exceptions.PropertiesDoesNotLoadException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelFileReader {
    /**
     * Obtiene los datos de un archivo de excel por el nombre de la hoja
     *
     * @param excelFilePath Ruta del libro de excel
     * @param sheetName     Nombre de la hoja que contiene los datos
     */
    public static List<Map<String, String>> getData(String excelFilePath, String sheetName) {
        Sheet sheet;
        try {
            sheet = getSheetByName(excelFilePath, sheetName);
        } catch (InvalidFormatException | IOException exception) {
            throw new PropertiesDoesNotLoadException(exception);
        }
        return readSheet(sheet);
    }

    /**
     * Obtiene los datos de un archivo de excel por el numero de la hoja
     *
     * @param excelFilePath Ruta del libro de excel
     * @param sheetNumber   Numero de la hoja que contiene los datos
     */
    public List<Map<String, String>> getData(String excelFilePath, int sheetNumber) {

        Sheet sheet;
        try {
            sheet = getSheetByIndex(excelFilePath, sheetNumber);
        } catch (InvalidFormatException | IOException exception) {
            throw new PropertiesDoesNotLoadException(exception);
        }
        return readSheet(sheet);
    }

    /**
     * Obtiene los datos de un archivo de excel por el nombre de la hoja
     *
     * @param excelFilePath Ruta del libro de excel
     * @param sheetName     Nombre de la hoja que contiene los datos
     */
    private static Sheet getSheetByName(String excelFilePath, String sheetName) throws IOException, InvalidFormatException {
        return getWorkBook(excelFilePath).getSheet(sheetName);
    }

    /**
     * Obtiene los datos de un archivo de excel por el numero de la hoja
     *
     * @param excelFilePath Ruta del libro de excel
     * @param sheetNumber   Numero de la hoja que contiene los datos
     */
    private Sheet getSheetByIndex(String excelFilePath, int sheetNumber) throws IOException, InvalidFormatException {
        return getWorkBook(excelFilePath).getSheetAt(sheetNumber);
    }

    /**
     * Retorna el libro correspondiente a la hoja determinada con antelacion
     *
     * @param excelFilePath Ruta del archivo de excel
     */
    private static Workbook getWorkBook(String excelFilePath) throws IOException, InvalidFormatException {
        return WorkbookFactory.create(new File(excelFilePath));
    }

    /**
     * Retorna la lista de tipo Map con todas las filas que contiene la hoja de excel
     *
     * @param sheet Hoja de excel
     */
    private static List<Map<String, String>> readSheet(Sheet sheet) {
        Row row;
        int totalRow = sheet.getPhysicalNumberOfRows();
        List<Map<String, String>> excelRows = new ArrayList<>();

        int headerRowNumber = getHeaderRowNumber(sheet);

        if (headerRowNumber != -1) {
            int totalColumn = sheet.getRow(headerRowNumber).getLastCellNum();
            int setCurrentRow = 1;
            for (int currentRow = setCurrentRow; currentRow <= totalRow; currentRow++) {
                row = getRow(sheet, sheet.getFirstRowNum() + currentRow);
                LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<>();
                for (int currentColumn = 0; currentColumn < totalColumn; currentColumn++) {
                    columnMapdata.putAll(getCellValue(sheet, row, currentColumn));
                }
                excelRows.add(columnMapdata);
            }
        }
        return excelRows;
    }

    /**
     * Obtiene el numero de filas concernientes al encabezado de la hoja
     */
    private static int getHeaderRowNumber(Sheet sheet) {
        Row row;
        int totalRow = sheet.getLastRowNum();
        for (int currentRow = 0; currentRow <= totalRow + 1; currentRow++) {
            row = getRow(sheet, currentRow);
            if (row != null) {
                int totalColumn = row.getLastCellNum();
                for (int currentColumn = 0; currentColumn < totalColumn; currentColumn++) {
                    Cell cell = row.getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (cell.getCellType() != CellType.BLANK || cell.getCellType() != CellType.FORMULA || cell.getCellType() != CellType._NONE) {
                        return row.getRowNum();
                    }
                }
            }
        }
        return (-1);
    }

    /**
     * Obtiene los datos de un numero de fila para una hoja especifica
     */
    private static Row getRow(Sheet sheet, int rowNumber) {
        return sheet.getRow(rowNumber);
    }

    /**
     * Obtiene el valor de cada una de las celdas de una hoja especifica
     */
    private static LinkedHashMap<String, String> getCellValue(Sheet sheet, Row row, int currentColumn) {
        LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<>();
        Cell cell;
        if (row == null) {
            if (sheet.getRow(sheet.getFirstRowNum()).getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() != CellType.BLANK) {
                String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(currentColumn).getStringCellValue();
                columnMapdata.put(columnHeaderName, "");
            }
        } else {
            cell = row.getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell indexCell = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            if (indexCell.getCellType() != CellType.BLANK) {
                String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex()).getStringCellValue();
                if (cell.getCellType() == CellType.STRING) {
                    columnMapdata.put(columnHeaderName, cell.getStringCellValue());
                } else if (cell.getCellType() == CellType.NUMERIC) {
                    columnMapdata.put(columnHeaderName, NumberToTextConverter.toText(cell.getNumericCellValue()));
                } else if (cell.getCellType() == CellType.BLANK) {
                    columnMapdata.put(columnHeaderName, "");
                } else if (cell.getCellType() == CellType.BOOLEAN) {
                    columnMapdata.put(columnHeaderName, Boolean.toString(cell.getBooleanCellValue()));
                } else if (cell.getCellType() == CellType.ERROR) {
                    columnMapdata.put(columnHeaderName, Byte.toString(cell.getErrorCellValue()));
                }
            }
        }
        return columnMapdata;
    }
}
