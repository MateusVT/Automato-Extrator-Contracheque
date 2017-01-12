package Automato;

import com.jaunt.ResponseException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author Torres
 */
public class Menu {

    public static void main(String[] args) throws ResponseException, ParserConfigurationException, IOException, FileNotFoundException, InvalidFormatException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Insira o Login : ");
        String login = scanner.nextLine();
        System.out.println("Insira a Senha : ");
        String senha = scanner.nextLine();
        System.out.println("Insira a data de inicio (aaaa-mm): ");
        String datai = scanner.nextLine() + "-27";
        System.out.println("Insira a data de fim (aaaa-mm): ");
        String dataf = scanner.nextLine() + "-27";
        System.out.println("Selecione o tipo : ");
        System.out.println("1 - FASPM");
        System.out.println("2 - Previdência");
        int atributo = Integer.parseInt(scanner.nextLine()); //1 = FASPM 2005 - 2016; 2 = Previdência 2004-2015;
        
        //Autenticação para Teste 
//        String login = "49204736";
//        String senha = "1806A";
//        String datai = "2008-06" + "-27";
//        String dataf = "2012-04" + "-27";
//        int atributo = 2;

        System.out.println();
        System.out.println("Start!");

        ParserExtrator extrator = new ParserExtrator(login, senha, datai, dataf, atributo);//Cria uma lista os valores extraidos
        extrator.run();
        GeradorPlanilhaPrev gerador = new GeradorPlanilhaPrev(extrator, extrator.getFirstValidDate(), dataf, atributo);//Escreve os dados na planilha
        gerador.run();

        System.out.println("Planilha : " + extrator.getNome() + ", PRONTA!");

    }

}
