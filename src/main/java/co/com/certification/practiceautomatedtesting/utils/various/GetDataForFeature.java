package co.com.certification.practiceautomatedtesting.utils.various;

import co.com.certification.practiceautomatedtesting.exceptions.PropertiesDoesNotLoadException;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static co.com.certification.practiceautomatedtesting.utils.constants.Constants.AT_PATTERN;
import static co.com.certification.practiceautomatedtesting.utils.constants.Constants.REPLACE_PATTERN;
import static java.nio.charset.StandardCharsets.UTF_8;

public class GetDataForFeature {
    public static final String UTILITY_CLASS = "Utility Class";
    protected static String[] dataVector;
    protected static List<Map<String, String>> excelData;

    private GetDataForFeature() {
        throw new IllegalStateException(UTILITY_CLASS);
    }

    public static List<String> readExcelFile(File featureFile) {
        List<String> fileData = new ArrayList<>();
        try (BufferedReader buffReader = new BufferedReader(
                new InputStreamReader(new BufferedInputStream(Files.newInputStream(featureFile.toPath())), UTF_8))) {
            String data;
            boolean replace = false;
            boolean externalData = false;
            String excelFilePath;
            String sheetName;
            while ((data = buffReader.readLine()) != null) {
                if (data.trim().contains(REPLACE_PATTERN)) {
                    fileData.add(data);
                    dataVector = getDataFilePath(data);
                    replace = true;
                    externalData = true;
                }
                if (replace) {
                    excelFilePath = dataVector[2].trim();
                    sheetName = dataVector[3].trim();
                    excelData = getDataSheetExcelFile(excelFilePath, sheetName);
                    String dataTable = createDataTable(excelData, Boolean.parseBoolean(dataVector[4].trim()));
                    fileData.add(dataTable);
                    replace = false;
                    continue;
                }
                externalData = writeLine(externalData, data);
                if (!externalData) {
                    fileData.add(data);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return fileData;
    }

    public static boolean writeLine(boolean externalData, String data) {
        if (externalData && !data.trim().contains("|")) {
            externalData = false;
        }
        return externalData;
    }

    public static List<Map<String, String>> getExcelData(List<Map<String, String>> excelData) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < excelData.size() - 1; i++) {
            mapList.add(excelData.get(i));
        }
        return mapList;
    }

    public static String createDataTable(List<Map<String, String>> excelData, boolean randomize) {
        StringBuilder stringBuilder = new StringBuilder();
        if (randomize) {
            Map<String, String> dataMap = excelData.get(Utils.getRandomNumberAnInterval(excelData.size() - 1));
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                stringBuilder.append("      | ").append(entry.getValue());
            }
            return stringBuilder.toString().concat("|");
        }else {
            List<Map<String, String>> mapList = getExcelData(excelData);
            for (Map<String, String> dataMap : mapList) {
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    stringBuilder.append("      | ").append(entry.getValue());
                }
                stringBuilder.append("|");
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        }
    }

    public static List<Map<String, String>> getDataSheetExcelFile(String excelFilePath, String sheetName) {
        try {
            excelData = ExcelFileReader.getData(excelFilePath, sheetName);
        } catch (Exception exception) {
            throw new PropertiesDoesNotLoadException(exception);
        }
        return excelData;
    }

    public static String[] getDataFilePath(String data) {
        return data.trim().split(AT_PATTERN);
    }

    public static void writeFeatureWithExcelData(String featurePath) {
        File file = new File(featurePath);
        List<String> featureWithData = GetDataForFeature.readExcelFile(file);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), UTF_8))) {
            for (String string : featureWithData) {
                writer.write(string);
                writer.write("\n");
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
