package co.com.certification.practiceautomatedtesting.utils.various;

import lombok.Getter;
import lombok.Setter;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static co.com.certification.practiceautomatedtesting.utils.constants.Constants.BUY_PRODUCTS_URL;

@Getter
@Setter
public class ManageParametersConnection {

    static Properties properties = new Properties();

    public static String getUrlBase() {
        String urlBase = "";
        String fileName = "./parameters.properties";
        try (FileReader fileReader = new FileReader(fileName)) {
            properties.load(fileReader);
            urlBase = properties.getProperty(BUY_PRODUCTS_URL);
        } catch (IOException ioException) {
            ioException.printStackTrace(System.err);
        }
        return urlBase;
    }
}