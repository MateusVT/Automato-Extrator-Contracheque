package Automato;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Torres
 */
public class LoginsSpreadSheetReader {

    FileInputStream file;
    XSSFWorkbook workbook;

    List<User> users = new ArrayList<>();

    public void lePlanilhaLogins() throws FileNotFoundException, IOException {
        int linha = 1, coluna = 0;
        file = new FileInputStream(new File("resources/Logins.xlsx").getAbsolutePath());
        workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);

        XSSFRow row1 = sheet.getRow(1);
        XSSFCell cell1 = row1.getCell(0);

        try {
            while (row1 != null) {
                row1 = sheet.getRow(linha);
                User user = new User(
                        row1.getCell(coluna).toString(),
                        row1.getCell(coluna + 1).toString(),
                        row1.getCell(coluna + 2).toString(),
                        row1.getCell(coluna + 3).toString() + "-27",
                        row1.getCell(coluna + 4).toString() + "-27",
                        (int) Float.parseFloat(row1.getCell(coluna + 5).toString()));
//                        Integer.parseInt(row1.getCell(coluna + 5).toString()));

                users.add(user);
                linha++;

            }

        } catch (NullPointerException e) {
        }

    }

    public List<User> getUsers() {
        return users;
    }

}
