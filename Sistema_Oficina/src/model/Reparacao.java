package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reparacao extends Manutencao {

    private List<Componente> listaComponentes;

    //Constructor para criar um servico de manutenção
    public Reparacao(String tipoServico, LocalDateTime dataInicio, String cliente, String funcionario, String equipamento){

        super(tipoServico , dataInicio , cliente , funcionario , equipamento);
        this.preco = 10.0;
        this.listaComponentes = new ArrayList<>();




    }
    //Constructor para servico de manutenção com mudança de preço
    public Reparacao(String tipoServico, LocalDateTime dataInicio, String cliente, String funcionario, String equipamento, double precoHora){
        super(tipoServico , dataInicio , cliente , funcionario , equipamento);
        this.preco = precoHora;
        this.listaComponentes = new ArrayList<>();




    }
    //Constructor de manutenção para adicionar componentes.
   Reparacao(String tipoServico ,LocalDateTime dataInicio , String cliente , String funcionario , String equipamento , int maxComponentes){
        super(tipoServico , dataInicio , cliente , funcionario , equipamento);
        this.preco= 10.0;


    }



}
