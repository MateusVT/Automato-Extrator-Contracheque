package Automato;

import java.io.FileNotFoundException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Torres
 */
public class GeradorPlanilhaPrev {

    int posicaoAtualListaValores = 0;//Controla a posição que está na Lista de Valores
    ParserExtrator user;
    String dataInicio;
    String dataFim;
    int atributo;//Define qual atributo será extraido

    FileInputStream file;
    XSSFWorkbook workbook;

    public GeradorPlanilhaPrev(ParserExtrator user, String dataInicio, String dataFim, int atributo) throws FileNotFoundException, IOException, InvalidFormatException {
        this.user = user;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.atributo = atributo;
    }

    public void run() throws IOException {

        lerModelo();

        if (user.getAtributo() == 1) {
            runFASPM();
        }
        if (user.getAtributo() == 2) {
            runPrev();
        }

        geraArquivo();

    }

    public void lerModelo() throws FileNotFoundException, IOException {

        if (user.getAtributo() == 1) {
            file = new FileInputStream(new File("resources/Modelo - FASPM.xlsx").getAbsolutePath());
        } else if (user.getAtributo() == 2) {
            file = new FileInputStream(new File("resources/Modelo - Previdência.xlsx").getAbsolutePath());
        }

        workbook = new XSSFWorkbook(file);
        workbook.setSheetName(0, user.getNome());

    }

    public void runFASPM() {

        String dateIni[] = dataInicio.split("-");//Cria vetor para separar dd;mm;aaaa
        String dateFim[] = dataFim.split("-");//Cria vetor para separar dd;mm;aaaa

        int mes = Integer.parseInt(dateIni[1]);//Primeiro mês válido a ser preenchido
        int anoini = Integer.parseInt(dateIni[0]);//Inicial : aaaa;
        int anofim = Integer.parseInt(dateFim[0]);//Final : aaaa;

        int linha = calculaLinhaFASPM(anoini);//Inicia na posição certa planilha
        int coluna = mes;

        while (anoini <= anofim) {//Roda todos os anos
            preencheFASPM(user, workbook, linha, coluna, mes);//Preenche a coluna começando pelo mês atribuido.
            anoini++;//Avança de ano
            linha += 2;//Calcula onde começa a preencher no novo ano.
            coluna = 1;
            mes = 1;//Reseta para o mês de janeiro.
        }
    }

    public void runPrev() {

        String dateIni[] = dataInicio.split("-");//Cria vetor para separar dd;mm;aaaa
        String dateFim[] = dataFim.split("-");//Cria vetor para separar dd;mm;aaaa

        int mes = Integer.parseInt(dateIni[1]);//Primeiro mês válido a ser preenchido
        int anoini = Integer.parseInt(dateIni[0]);//Inicial : aaaa;
        int anofim = Integer.parseInt(dateFim[0]);//Final : aaaa;

        int linha = mes + calculaLinhaPrev(anoini);//Inicia na posição certa planilha
        int coluna;

        while (anoini <= anofim) {//Roda todos os anos
            coluna = calculaColunaPrev(anoini);//Dado o ano calcula a coluna que será preenchida
            preenchePREV(user, workbook, linha, coluna, mes);//Preenche a coluna começando pelo mês atribuido.
            anoini++;//Avança de ano
            linha = calculaLinhaPrev(anoini) + 1;//Calcula onde começa a preencher no novo ano.
            mes = 1;//Reseta para o mês de janeiro
        }

    }

    public void preencheFASPM(ParserExtrator user, XSSFWorkbook workbook, int linha, int coluna, int mes) {

        XSSFSheet sheet = workbook.getSheetAt(0);

        while (mes <= 12) {
            if (posicaoAtualListaValores < user.getListaValores().size()) {

                XSSFRow row1 = sheet.getRow(linha);
                XSSFCell cell1 = row1.getCell(coluna);
                if (cell1 == null) {
                    return;
                }
                cell1.setCellValue(Float.valueOf(user.getListaValores().get(posicaoAtualListaValores)));

                posicaoAtualListaValores++;
                mes++;
                coluna++;
            } else {
                return;
            }
        }

    }

    public void preenchePREV(ParserExtrator user, XSSFWorkbook workbook, int linha, int coluna, int mes) {

        XSSFSheet sheet = workbook.getSheetAt(0);

        while (mes <= 12) {
            if (posicaoAtualListaValores < user.getListaValores().size()) {

                XSSFRow row1 = sheet.getRow(linha);
                XSSFCell cell1 = row1.getCell(coluna);
                cell1.setCellValue(Float.valueOf(user.getListaValores().get(posicaoAtualListaValores)));
                posicaoAtualListaValores++;
                mes++;//Avança o mês a ser escrito
                linha++;//Avança a linha que vai escrever
            } else {
                return;
            }
        }

    }

    public void geraArquivo() throws FileNotFoundException, IOException {

        workbook.setForceFormulaRecalculation(true);//Atualiza as formúlas pré-existentes na planilha.
        FileOutputStream filewrite = null;

        if (user.getAtributo() == 1) {
            filewrite = new FileOutputStream(new File("gerados/" + user.getNome() + " - FASPM.xlsx").getAbsolutePath());
        } else if (user.getAtributo() == 2) {
            filewrite = new FileOutputStream(new File("gerados/" + user.getNome() + " - Previdência.xlsx").getAbsolutePath());
        }

        workbook.write(filewrite);

        filewrite.close();
        file.close();

    }

    public int calculaLinhaPrev(int anoini) {
        switch (anoini) {
            case 2004:
            case 2005:
            case 2006:
                return 0;
            case 2007:
            case 2008:
            case 2009:
                return 17;
            case 2010:
            case 2011:
            case 2012:
                return 34;
            case 2013:
            case 2014:
            case 2015:
                return 51;
            default:
                return 0;
        }
    }

    public int calculaColunaPrev(int anoini) {
        switch (anoini) {
            case 2004:
            case 2007:
            case 2010:
            case 2013:
                return 1;
            case 2005:
            case 2008:
            case 2011:
            case 2014:
                return 7;
            case 2006:
            case 2009:
            case 2012:
            case 2015:
                return 13;
            default:
                return 1;
        }
    }

    public int calculaLinhaFASPM(int anoini) {
        return ((anoini - 2005) * 2) + 1;
    }

    public int calculaColunaFASPM(int anoini) {
        return 0;
    }

}
