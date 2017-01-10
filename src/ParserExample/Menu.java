package ParserExample;

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

//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Insira o Login : ");
//        String login = scanner.nextLine();
//        System.out.println("Insira a Senha : ");
//        String senha = scanner.nextLine();
//        System.out.println("Insira a data de inicio (aaaa-mm): ");
//        String datai = scanner.nextLine() + "-27";
//        System.out.println("Insira a data de fim (aaaa-mm): ");
//        String dataf = scanner.nextLine() + "-27";

//        String login = "42176095";
//        String senha = "L325920";
        String login = "51665279";
        String senha = "mat122";
//        String login = "33621930";
//        String senha = "1A2B";
        String datai = "2006-01" + "-27";
        String dataf = "2006-12" + "-27";


        System.out.println("Valores :");
//        ParserExtratorFASPM user = new ParserExtratorFASPM(login, senha, datai, dataf);
        ParserExtratorPrev user = new ParserExtratorPrev(login, senha, datai, dataf);
        GeradorPlanilhaPrev gerador = new GeradorPlanilhaPrev(user, datai, dataf);//Escreve os dados na planilha


    }

}
