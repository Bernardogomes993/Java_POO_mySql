package banco.model;

public class Cliente {

    private final String nome;
    private final String nif;

    public Cliente(String nome , String nif){

        this.nome = nome;
        this.nif = nif;

    }


    public String getNome(){
        return this.nome;
    }

    public String getNif(){
        return this.nif;
    }

    @Override

    public String toString(){
        return nome + "(Nif: " + nif + ")";
    }

}