/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParserExample;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author Torres
 */
public class ParserExtratorFASPM {

    //URL principal
    public static final String urlMain = "https://wwws.portaldoservidor.pr.gov.br/ccheque/";

    //Dados de entrada do programa
    public String login;
    public String senha;
    public String dataInicio;
    public String dataFim;
    public String mes;
    public String ano;
    public String tipo;

    //Dados de acesso a sessão
    public String id = null;
    public String datap = null;
    public String tipop = null;
    public String tipoi = null;
    public String rg = null;
    public String ufrg = null;
    public String organismo = null;

    public static String nome; //Nome do funcionário
    public String error; //String que informa o erro ocorrido
    public List<String> valores = new ArrayList<>(); //Lista com todos os valores extraidos

    public ParserExtratorFASPM(String login, String senha, String dataInicio, String dataFim) throws ParserConfigurationException, IOException {

//        tipo = "Base Previdência:";
//        tipo = "FASPM";
        this.login = login;
        this.senha = senha;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;

        //Percorre todos os anos no intervalo de dataInicio e dataFim
        while (!dataInicio.equals(getNextDate(dataFim))) {
            System.out.println(dataInicio);
            getValor(dataInicio); //Extrai o valor da data atribuida
            dataInicio = getNextDate(dataInicio); //Avança para a próxima data. (mês)
        }

    }

    public void getValor(String datai) throws IOException, ParserConfigurationException {
        try {
            Document contraCheque;
            Document contraChequeDados;

            Connection.Response urlLogin = Jsoup.connect(urlMain)//Acessa a página principal
                    .timeout(10000)
                    .method(Connection.Method.GET)
                    .execute();

            Jsoup.connect(urlMain + "ValidaSenha.asp?tela=4")//Realiza a autenticação com os dados de entrada 
                    .timeout(10000)
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

            contraChequeDados = (Document) Jsoup.connect(urlMain + "usuarios/sel_ccheque2.asp")//Retorna o html da página secundária
                    .timeout(10000)
                    .cookies(urlLogin.cookies())
                    .get();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Elements root = contraChequeDados.getElementsByTag("a"); //Filtra o documento retornando os elesmentos de tag <a>

            getDadosSessão(root); //Obtém os dados representados por "*" da URL : "https://wwws.portaldoservidor.pr.gov.br/ccheque/usuarios/contrachequeM4.asp?id=*****&datap=****-**-**&datai=****-**-**&tipop=*&tipoi=*&rg=***************&UfRg=**&organismo=**"

            contraCheque = (Document) Jsoup.connect(urlMain + "usuarios/contrachequeM4.asp") //Retorna o html do boletim de contracheque
                    .timeout(10000)
                    .data("id", id) //Dados de autenticação extraidos a página secundária pelo método getDadosSessão
                    .data("datap", datap)
                    .data("datai", datai)
                    .data("tipop", tipop)
                    .data("tipoi", tipoi)
                    .data("rg", rg)
                    .data("UfRg", ufrg)
                    .data("organismo", organismo) //
                    .cookies(urlLogin.cookies())
                    .get();

            DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder1 = factory1.newDocumentBuilder();

            Elements root1 = contraCheque.getElementsByTag("font"); //Filtra o código html do boletim retornando os elesmentos de tag <font>

            String contraChequeString = root1.toString()//Remove alguns elementos para facilitar a visualização do dado desejado "Base previdência: xxxx,xx" e criar um padrão para a extração.
                    .replaceAll("<font face=\"Arial\" size=\"1\">", "")
                    .replaceAll("<font face=\"Arial\" size=\"2\">", "")
                    .replaceAll("</font>", "")
                    .replaceAll("&nbsp;", "")
                    .replaceAll("\\.", "")
                    .replaceAll(",", ".")
                    .replaceAll(":\n", ": ")
                    .replaceAll("\n\n", "\n")
                    .replaceAll("\n\n", "\n").replaceAll(": ", ":\n");

            int indexPrev = contraChequeString.indexOf("Funcionário:");//Encontra a posição da String desejada. (Nome do funcionário)
            String[] split = contraChequeString.substring(indexPrev + 13, indexPrev + 35).split("\\s+");//Dado o index pega os caracteres do intervalo após o fim da String "Funcionário:"
            nome = split[0] + " " + split[1];//Pega os dois primeiros nomes do funcionario.

            int index = contraChequeString.indexOf("Previdência:");//Encontra a posição da String desejada. ("Previdência" != "Previdência:")
            String preValor = contraChequeString.substring(index + 13, index + 20);//Dado o index pega os 7 caracteres após o fim da String "Previdência:"
            String valor = preValor.replaceAll("[^\\d.]+|\\.(?!\\d)", "");//Remove os lixos que podem vir do preValor, pois o salário pode variar.

            System.out.println(valor + " R$");
            valores.add(valor);

        } catch (StringIndexOutOfBoundsException | NullPointerException | IllegalArgumentException e) {//Erros que não possuem tratamento, apenas informa o usuário o motivo do erro.
            error = "A data " + datai.replaceAll("=", "") + " não possui contracheque.";
            valores.add("0");
            System.out.println(error);
        } catch (SocketException e) {
            error = "Conexão com a internet perdida.";
            valores.add("0");
            System.out.println(error);
        }

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

    public void getDadosSessão(Elements root) {

        if ((root.size() == 3)) {
            id = root.get(1).toString().substring(62, 71).replaceAll("[^\\d.]+|\\.(?!\\d)", "");
            datap = root.get(1).toString().substring(80, 90);
//            datai = root.get(1).toString().substring(102, 111);
            tipop = root.get(1).toString().substring(122, 123);
            tipoi = root.get(1).toString().substring(134, 135);
            rg = root.get(1).toString().substring(143, 158);
            ufrg = root.get(1).toString().substring(168, 170);
            organismo = root.get(1).toString().substring(185, 187);
        }

        if (root.size() > 3 && (root.get(1).toString().substring(122, 124).equals("1&"))) {
            id = root.get(1).toString().substring(62, 71).replaceAll("[^\\d.]+|\\.(?!\\d)", "");
            datap = root.get(1).toString().substring(80, 90);
//            datai = root.get(1).toString().substring(102, 111);
            tipop = root.get(1).toString().substring(122, 123);
            tipoi = root.get(1).toString().substring(134, 135);
            rg = root.get(1).toString().substring(143, 158);
            ufrg = root.get(1).toString().substring(168, 170);
            organismo = root.get(1).toString().substring(185, 187);
        }

        if ((root.size() > 3) && !(root.get(1).toString().substring(122, 124).equals("1&"))) {
            id = root.get(2).toString().substring(62, 71).replaceAll("[^\\d.]+|\\.(?!\\d)", "");
            datap = root.get(2).toString().substring(80, 90);
//            datai = root.get(2).toString().substring(102, 111);
            tipop = root.get(2).toString().substring(122, 123);
            tipoi = root.get(2).toString().substring(134, 135);
            rg = root.get(2).toString().substring(143, 158);
            ufrg = root.get(2).toString().substring(168, 170);
            organismo = root.get(2).toString().substring(185, 187);
        }

    }

    public List<String> getValores() {
        return valores;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatap() {
        return datap;
    }

    public void setDatap(String datap) {
        this.datap = datap;
    }

    public String getTipop() {
        return tipop;
    }

    public void setTipop(String tipop) {
        this.tipop = tipop;
    }

    public String getTipoi() {
        return tipoi;
    }

    public void setTipoi(String tipoi) {
        this.tipoi = tipoi;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getUfrg() {
        return ufrg;
    }

    public void setUfrg(String ufrg) {
        this.ufrg = ufrg;
    }

    public String getOrganismo() {
        return organismo;
    }

    public void setOrganismo(String organismo) {
        this.organismo = organismo;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
