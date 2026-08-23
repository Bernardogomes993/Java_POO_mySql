package model;

public class Componente {

    private int id;
    private int servicoId;
    private String nome;
    private double preco;

    public Componente(String nome , double preco){

        this.nome = nome;
        this.preco = preco;


    }

    public Componente(int id , int servicoId , String nome , double preco){

        this.id = id;
        this.servicoId = servicoId;
        this.nome = nome;
        this.preco = preco;
    }

    public int getId(){
        return  this.id;
    }

    public int getServicoId(){
        return  this.servicoId;

    }

    public String getNome(){
        return this.nome;
    }

    public double getPreco(){
        return this.preco;
    }

    @Override

    public String toString(){
        return "Componente: " + nome + " | Preço: " + String.format("%.2f €", preco);
    }
}
