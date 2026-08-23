package model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class Servico  implements Serializable {
    protected double preco;
    protected String tipoServico;
    protected LocalDateTime dataInicio;
    protected LocalDateTime dataFim;
    protected String cliente;
    protected String funcionario;
    protected String equipamento;
    protected String[] observacoes = new String[20];
    protected double[] precoComponentes = new double[20];
    protected boolean pago;
    protected boolean concluido;
    protected static int numeroServicos;
    protected int codigoServico;

   public Servico(String tipoServico , LocalDateTime dataInicio , String cliente , String funcionario , String equipamento){

        this.tipoServico = tipoServico;
        this.dataInicio = dataInicio;
        this.cliente = cliente;
        this.funcionario = funcionario;
        this.equipamento = equipamento;
        this.dataFim = null;

        numeroServicos++;
        this.codigoServico = numeroServicos;
        this.pago = false;
        this.concluido = false;

    }


    public String getTipoServico(){return tipoServico;}
    public LocalDateTime getDataInicio(){return dataInicio;}
    public LocalDateTime getDataFim(){
        return this.dataFim;
    }
    public String getCliente(){
        return this.cliente;
    }
    public String getFuncionario(){return funcionario;}
    public String getEquipamento(){return  equipamento;}
    public double getPreco(){return preco;}
    public boolean isPago(){return pago;}
    public boolean isConcluido(){return concluido;}
    public int getCodigoServico(){return codigoServico;}

    public void setCodigoServico(int codigoServico) { this.codigoServico = codigoServico; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }



    public abstract double calcularValorPagar();
    public abstract void concluirServico();
    public abstract void adicionarComponentes(String nomeComponente , double precoComponente);
    public abstract  void displayComponentes();
    public abstract void atualizarPreco(double novoPreco);
    public abstract void preencherDataFim(LocalDateTime dataFim);

    public void pagarServico(){
        this.pago = true;
        System.out.println("Serviço #" + this.codigoServico + " foi marcado como PAGO!");
    }






    public double getDuracaoEmHoras() {
        if (this.dataInicio == null || this.dataFim == null || !this.concluido) {
            return Double.MAX_VALUE; // Mantém para a ordenação funcionar
        }
        Duration duracao = Duration.between(this.dataInicio, this.dataFim);
        return duracao.toMinutes() / 60.0;
    }

    // 2. Usado para IMPRIMIR no ecrã sem mostrar o número gigante
    public String getDuracaoFormatada() {
        if (!this.concluido || this.dataFim == null) {
            return "Em curso (não concluído)";
        }
        return String.format("%.2f horas", getDuracaoEmHoras());
    }



    @Override
    public String toString(){
        boolean estaConcluido = (this.dataFim != null);
        return "Tipo de model.Servico: " + this.tipoServico +
                " | Codigo: " + this.codigoServico + " | Concluido: " + this.concluido;

    }




}
