package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Manutencao extends Servico {


    private List<Componente> listaComponentes;


//Constructor para criar um servico de manutenção
  public Manutencao(String tipoServico , LocalDateTime dataInicio , String cliente , String funcionario , String equipamento){
        super(tipoServico , dataInicio , cliente , funcionario , equipamento);
        this.preco = 5.0;
        this.listaComponentes = new ArrayList<>();

    }
//Constructor para servico de manutenção com mudança de preço
public Manutencao(String tipoServico, LocalDateTime dataInicio, String cliente, String funcionario, String equipamento, double precoHora){
        super(tipoServico , dataInicio , cliente , funcionario , equipamento);
        this.preco = precoHora;
        this.listaComponentes = new ArrayList<>();

    }


    @Override
    public  void  atualizarPreco(double novoPreco){

       this.preco = novoPreco;
        System.out.println("O preço por hora de " + tipoServico + " foi atualizado para: " + novoPreco + "€");
    }

    @Override
    public void adicionarComponentes(String nomeComponente , double precoComponente){

        if(this.concluido){
            System.out.println("❌ Não é possível adicionar componentes a um serviço concluído!");
            return;
        }

        Componente novo = new Componente(nomeComponente , precoComponente);
        this.listaComponentes.add(novo);
        System.out.println("✅ Componente '" + nomeComponente + "' adicionado com sucesso!");

    }



    @Override
    public void displayComponentes(){
      if(listaComponentes.isEmpty()){
          System.out.println("Não existem componentes associados.");
          return;
      }
        System.out.println("Componentes substituídos:");
        for (Componente c : listaComponentes) {
            System.out.println(" - " + c.getNome() + " (" + String.format("%.2f €", c.getPreco()) + ")");
        }
    }

    @Override
    public double calcularValorPagar(){
      if(!this.concluido){
          System.out.println("⚠️ O serviço tem de estar concluído para calcular o valor.");
          return 0.0;
      }

      double totalComponentes = 0.0;
      for(Componente c : listaComponentes){

          totalComponentes += c.getPreco();
      }
      return (getDuracaoEmHoras() * this.preco) + totalComponentes;
    }

    @Override
    public void preencherDataFim(LocalDateTime dataFim){
       this.dataFim = dataFim;

    }
    @Override

    public void concluirServico(){

       if (this.dataInicio == null || this.dataFim == null) {
            System.out.println("❌ Erro: É necessário indicar a data de fim primeiro!");
            return;
        }

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

    public List<Componente> getListaComponentes (){
       return this.listaComponentes;

    }

    public void setListaComponentes(List <Componente> lista){
      this.listaComponentes = lista;

    }

}
