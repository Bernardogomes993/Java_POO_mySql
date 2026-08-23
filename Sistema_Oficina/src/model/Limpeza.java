package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Limpeza  extends Servico {



    //Constructor para criar um servico de manutenção
    public Limpeza(String tipoServico, LocalDateTime dataInicio, String cliente, String funcionario, String equipamento){

        super(tipoServico , dataInicio , cliente , funcionario , equipamento);
        this.preco = 15.0;

        this.observacoes = new String[0];
        this.precoComponentes = new double[0];

    }

    @Override

    public double calcularValorPagar(){

        if(!this.concluido){
            System.out.println("O servico tem de estar concluido");
            return 0.0;
        }
        else{
            return this.preco;
        }
    }

    @Override
    public  void  atualizarPreco(double novoPreco){

        this.preco = novoPreco;
        System.out.println("O preço por hora de " + tipoServico + " foi atualizado para: " + novoPreco + "€");
    }


    @Override
    public void adicionarComponentes(String nomeComponente , double precoComponente){
        System.out.println("Nao e possivel adicionar componentes!");
    }

    @Override
    public void displayComponentes(){

        System.out.println("Não existem componentes !");
    }

    @Override
    public void preencherDataFim(LocalDateTime dataFim){
        this.dataFim = dataFim;

    }

    @Override
    public void concluirServico(){
        Duration duracao = Duration.between(this.dataInicio , this.dataFim);
        double horas = duracao.toMinutes() / 60.0;
        if(horas <= 0){
            System.out.println("Nao e possivel concluir o servico!");
        }
        else{
            this.concluido = true;
            System.out.println("Serviço #" +
                    this.codigoServico + " foi marcado como Concluido!");
        }
    }
}
