package Automato;

import com.jaunt.ResponseException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author Torres
 */
public class Menu {

    public static void main(String[] args) throws ResponseException, ParserConfigurationException, IOException, FileNotFoundException, InvalidFormatException {

        AutenticationType autetication = new AutenticationType();

//        autetication.multiLogin();

        autetication.loginOneByOne();

    }

}
