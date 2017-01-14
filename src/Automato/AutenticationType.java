/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Automato;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author Torres
 */
public class AutenticationType {

    public void loginOneByOne() throws IOException, ParserConfigurationException, FileNotFoundException, InvalidFormatException {

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

        System.out.println("Start!");

        ParserExtrator extrator = new ParserExtrator(login, senha, datai, dataf, atributo);

        extrator.run();

        SpreadSheetGenerator gerador = new SpreadSheetGenerator(extrator, extrator.getFirstValidDate(), dataf, atributo);//Escreve os dados na planilha

        gerador.run();

        System.out.println("Planilha : " + extrator.getNome() + ", PRONTA!");
    }

    public void multiLogin() throws IOException, ParserConfigurationException, FileNotFoundException, InvalidFormatException {

        System.out.println("Start!");

        LoginsSpreadSheetReader logins = new LoginsSpreadSheetReader();

        logins.lePlanilhaLogins();

        int i = 0;

        while (i < logins.getUsers().size()) {

            ParserExtrator extrator = new ParserExtrator(logins.getUsers().get(i).getRg(),
                    logins.getUsers().get(i).getSenha(),
                    logins.getUsers().get(i).getDataInicio(),
                    logins.getUsers().get(i).getDataFim(),
                    logins.getUsers().get(i).getTipo());//Cria uma lista os valores extraidos

            extrator.run();

            SpreadSheetGenerator gerador = new SpreadSheetGenerator(extrator,
                    extrator.getFirstValidDate(),
                    logins.getUsers().get(i).getDataFim(),
                    logins.getUsers().get(i).getTipo());//Escreve os dados na planilha

            gerador.run();

            System.out.println("Planilha : " + extrator.getNome() + ", PRONTA!\n");
            i++;
        }

    }
}
