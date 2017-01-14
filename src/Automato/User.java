/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Automato;

/**
 *
 * @author Torres
 */
public class User {

    private String nome;
    private String rg;
    private String senha;
    private String dataInicio;
    private String dataFim;
    private int tipo;

    public User(String nome, String rg, String senha, String dataInicio, String dataFim, int tipo) {
        this.nome = nome;
        this.rg = rg;
        this.senha = senha;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getRg() {
        return rg;
    }

    public String getSenha() {
        return senha;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public int getTipo() {
        return tipo;
    }

}
