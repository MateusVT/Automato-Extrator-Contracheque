package ParserExample;

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

    int valor = 0;

    public GeradorPlanilhaPrev(ParserExtratorPrev user, String dataInicio, String dataFim) throws FileNotFoundException, IOException, InvalidFormatException {

//        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
//        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//        Stream<String> lines = bufferedReader.lines();
//        Iterator<String> iterator = lines.iterator();
        FileInputStream file = new FileInputStream(new File("resources/Modelo - Previdência.xlsx").getAbsolutePath());
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        workbook.setSheetName(0, user.getNome());
        String dateIni[] = dataInicio.split("-");
        String dateFim[] = dataFim.split("-");

        int i = 0;
        int lasti = 0;
        int mes = Integer.parseInt(dateIni[1]);
        int anoini = Integer.parseInt(dateIni[0]);
        int anofim = Integer.parseInt(dateFim[0]);
        int inicio = calculaLinha(anoini);
        int coluna = calculaColuna(anoini);
        int linha = mes + inicio;
        int sizeLista = user.getValores().size();

        while (anoini <= anofim) {
            inicio = calculaLinha(anoini);
            coluna = calculaColuna(anoini);
            Escreve(user, workbook, linha, coluna, lasti);
            anoini++;
            linha = calculaLinha(anoini) + 1;
        }

        file.close();
        FileOutputStream filewrite = new FileOutputStream(new File("resources/" + user.getNome() + " - Previdência.xlsx").getAbsolutePath());
        workbook.write(filewrite);
        filewrite.close();
    }

    public void Escreve(ParserExtratorPrev user, XSSFWorkbook workbook, int linha, int coluna, int lasti) {
        int i = 0;
        XSSFSheet sheet = workbook.getSheetAt(0);
        while (i < 12) {
            if (valor < user.getValores().size()) {
                XSSFRow row1 = sheet.getRow(linha);
                XSSFCell cell1 = row1.getCell(coluna);
                cell1.setCellValue(Float.valueOf(user.getValores().get(valor)));
                valor++;
                i++;
                linha++;
            } else {
                return;
            }
        }

    }

    public int calculaLinha(int anoini) {
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

    public int calculaColuna(int anoini) {
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

}
