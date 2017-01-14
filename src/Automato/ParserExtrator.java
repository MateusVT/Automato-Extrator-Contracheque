package Automato;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import javax.xml.parsers.ParserConfigurationException;
import org.jsoup.HttpStatusException;

/**
 *
 * @author Torres
 */
public class ParserExtrator {

    //URL principal
    public static final String URL_MAIN = "https://wwws.portaldoservidor.pr.gov.br/ccheque/";

    //Dados de entrada do programa
    private String login;
    private String senha;
    String dataInicio;
    String dataFim;
    int atributo; //1 = FASPM; 2 = Previdência;

    //Dados de acesso a sessão
    private String id = null;
    private String datap = null;
    private String tipop = null;
    private String tipoi = null;
    private String rg = null;
    private String ufrg = null;
    private String organismo = null;

    //Utilitários 
    boolean firstValidDateCheck = true; //Usado para pegar apenas a primeira data com dados disponivéis para começar o loop.
    String firstValidDate; //Armazena a primeira data válida.
    String regularExpressionToInt = "[^\\d.]+|\\.(?!\\d)";

    Connection.Response urlLogin;//Contém a conexão efetuada no primeiro login  
    public static String nome; //Nome do funcionário
    private final List<String> listaValores = new ArrayList<>(); //Lista com todos os listaValores extraidos
    String error; //String que informa o erro ocorrido

    //Armazenam os documentos extraidos
    Document boletoContracheque;
    Document paginaSecundaria;

    public ParserExtrator(String login, String senha, String dataInicio, String dataFim, int atributo) {

        this.atributo = atributo;
        this.login = login;
        this.senha = senha;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;

    }

    public void run() throws IOException, ParserConfigurationException {

        conectaMain(dataInicio);
        while (!dataInicio.equals(getNextDate(dataFim))) {
            System.out.println(dataInicio);
            extrairValor(dataInicio); //Extrai o valor da data atribuida
            dataInicio = getNextDate(dataInicio); //Avança para a próxima data. (mês)
        }

    }

    public void conectaMain(String datai) throws IOException, ParserConfigurationException {

        try {

            urlLogin = Jsoup.connect(URL_MAIN)//Acessa a página principal
                    .timeout(10000)
                    .method(Connection.Method.GET)
                    .execute();

        } catch (SocketException | SocketTimeoutException e) {
            error = "Conexão com a internet perdida.\n" + "Execute o software novamente!";
            System.out.println(error);
            System.exit(0);
        }
    }

    public void extrairValor(String datai) throws IOException, ParserConfigurationException {
        if (firstValidDateCheck) {
            firstValidDateCheck = false;//Disabilita na primeira vez que entrar no método
            firstValidDate = datai;
        }

        try {

            Jsoup.connect(URL_MAIN + "ValidaSenha.asp?tela=4")//Realiza a autenticação com os dados de entrada 
                    .timeout(20000)
                    .data("cookieexists", "true")
                    .data("refer", "02/2007")
                    .data("edUsuario", login)
                    .data("edUfUsuario", "PR")
                    .data("edSenha", senha)
                    .data("edmes", datai.substring(5, 7))//dataInicio = aaaa-mm-dd
                    .data("edano", datai.substring(0, 4))//dataInicio = aaaa-mm-dd
                    .data("btEntrar1", "entrar1(this.form)")//btEntrar1 = Contracheque
                    .cookies(urlLogin.cookies())
                    .post();

            paginaSecundaria = (Document) Jsoup.connect(URL_MAIN + "usuarios/sel_ccheque2.asp")//Retorna o html da página secundária
                    .timeout(20000)
                    .cookies(urlLogin.cookies())
                    .get();

            Elements elementsTagA = paginaSecundaria.getElementsByTag("a"); //Filtra o documento retornando os elesmentos de tag <a>

            getDadosSessão(elementsTagA); //Obtém os dados representados por "*" da URL : "https://wwws.portaldoservidor.pr.gov.br/ccheque/usuarios/contrachequeM4.asp?id=*****&datap=****-**-**&datai=****-**-**&tipop=*&tipoi=*&rg=***************&UfRg=**&organismo=**"

            boletoContracheque = getHtmlContracheque(datai);

            Elements elementsTagFont = boletoContracheque.getElementsByTag("font"); //Filtra o código html do boletim retornando os elesmentos de tag <font>

            String boletoContrachequeString = filtraElementos(elementsTagFont);//Remove alguns elementos para facilitar a visualização do dado desejado "Base previdência: xxxx,xx" e criar um padrão para a extração.

            String preValor = null; //Valor extraido do intervalo, provavelmente sujo com caracteres especiais

            if (atributo == 1) {
                int index = boletoContrachequeString.indexOf("6206 Fundo de Assistência a Saúde da Polícia Militar do Paraná");//Encontra a posição da String desejada.
                preValor = boletoContrachequeString.substring(index + 63, index + 68);//Dado o index pega os 5 caracteres após o fim da String "FASPM:"
//           preValor = contraChequeString.substring(index + 57, index + 63); Sem o 6206

            } else if (atributo == 2) {
                int index = boletoContrachequeString.indexOf("Previdência:");//Encontra a posição da String desejada. ("Previdência" != "Previdência:")
                preValor = boletoContrachequeString.substring(index + 13, index + 20);//Dado o index pega os 7 caracteres após o fim da String "Previdência:"
            }

            String valor = preValor.replaceAll(regularExpressionToInt, "");//Remove os lixos que podem vir do preValor, pois o salário pode variar.

            if ("".equals(valor)) {
                valor = "0";
            }

            System.out.println(valor + " R$"); //Imprime os valores de cada data válida.
            listaValores.add(valor);

        } catch (StringIndexOutOfBoundsException | NullPointerException | IllegalArgumentException | HttpStatusException e) {//Erros que não possuem tratamento, apenas informa o usuário o motivo do erro.
            listaValores.add("0");
            error = "A data " + datai.replaceAll("=", "") + " não possui contracheque.";
            System.out.println(error);
        } catch (SocketException | SocketTimeoutException e) {//Devido a perca de dados resultade da falta de conexão o programa é encerrado para não gerar um arquivo incompleto.
            error = "Conexão com a internet perdida.\n" + "Execute o software novamente!";
            System.out.println(error);
            System.exit(0);
        }

    }

    public void getDadosSessão(Elements elementsTag) {
        int i = 0;
        try {
            while (!elementsTag.get(i).toString().substring(122, 124).equals("1&")) {
                i++;
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Os dados de autenticação estão incorretos.");
            System.exit(0);
        }
        id = elementsTag.get(i).toString().substring(62, 71).replaceAll(regularExpressionToInt, "");
        datap = elementsTag.get(i).toString().substring(80, 90);
//      datai = root.get(1).toString().substring(102, 111);
        tipop = elementsTag.get(i).toString().substring(122, 123);
        tipoi = elementsTag.get(i).toString().substring(134, 135);
        rg = elementsTag.get(i).toString().substring(143, 158);
        ufrg = elementsTag.get(i).toString().substring(168, 170);
        organismo = elementsTag.get(i).toString().substring(185, 187);

//        if ((elementsTag.size() == 3)) {}
//        if (elementsTag.size() > 3 && (elementsTag.get(1).toString().substring(122, 124).equals("1&"))) {}
//        if ((elementsTag.size() > 3) && !(elementsTag.get(1).toString().substring(122, 124).equals("1&"))) {}
//        if ((elementsTag.size() > 4) && (elementsTag.get(3).toString().substring(122, 124).equals("1&"))) {}
    }

    public Document getHtmlContracheque(String datai) throws IOException {

        return Jsoup.connect(URL_MAIN + "usuarios/contrachequeM4.asp") //Retorna o html do boleto de contracheque
                .timeout(20000)
                .data("id", id) //Dados de autenticação extraidos a página secundária pelo método getDadosSessão
                .data("datap", datap)
                .data("datai", datai)
                .data("tipop", tipop)
                .data("tipoi", tipoi)
                .data("rg", rg)
                .data("UfRg", ufrg)
                .data("organismo", organismo)//
                .cookies(urlLogin.cookies())
                .get();

    }

    public String filtraElementos(Elements elementsTag) {
        return elementsTag.toString()//Remove alguns elementos para facilitar a visualização do dado desejado "Base previdência: xxxx,xx" e criar um padrão para a extração.
                .replaceAll("<font face=\"Arial\" size=\"1\">", "")
                .replaceAll("<font face=\"Arial\" size=\"2\">", "")
                .replaceAll("</font>", "")
                .replaceAll("&nbsp;", "")
                .replaceAll("\\.", "")
                .replaceAll(",", ".")
                .replaceAll(":\n", ": ")
                .replaceAll("\n", " ")
                .replaceAll(": ", ":\n")
                .replaceAll("       ", "\n")
                .replaceAll("  ", "\n");

    }

    public String getNextDate(String dataInicio) {
        String date[] = dataInicio.split("-");
        String nextDate = null;

        int ano = Integer.parseInt(date[0]);
        int mes = Integer.parseInt(date[1]);
        int dia = Integer.parseInt(date[2]);

        if (mes < 12) {
            mes += 1;
            if (mes < 10) {
                nextDate = ano + "-" + "0" + mes + "-" + dia;
            } else {
                nextDate = ano + "-" + mes + "-" + dia;
            }
            return nextDate;
        }
        if (mes == 12) {
            ano += 1;
            nextDate = ano + "-" + "01" + "-" + dia;
        }
        return nextDate;
    }

    public String getNome() {
        Elements elementsTagFont = null;

        try {
            elementsTagFont = boletoContracheque.getElementsByTag("font"); //Filtra o código html do boletim retornando os elesmentos de tag <font>
        } catch (NullPointerException e) {
            error = "Dados do usuário incorretos.";
            System.out.println(error);
            System.exit(0);
        }

        String boletoContrachequeString = filtraElementos(elementsTagFont);

        int indexNome = boletoContrachequeString.indexOf("Funcionário:");//Encontra a posição da String desejada. (Nome do funcionário)
        String[] split = boletoContrachequeString.substring(indexNome + 13, indexNome + 35).split("\\s+");//Dado o index pega os caracteres do intervalo após o fim da String "Funcionário:"

        nome = split[0] + " " + split[1];//Pega os dois primeiros nomes do funcionario.

        if (split[1].equals("DE")) {
            nome = split[0] + " " + split[1] + " " + split[2];//Pega os três primeiros nomes do funcionario.
        }

        return nome;
    }

    public List<String> getListaValores() {
        return listaValores;
    }

    public String getFirstValidDate() {
        return firstValidDate;
    }

    public int getAtributo() {
        return atributo;
    }

    public String getError() {
        return error;
    }

}
