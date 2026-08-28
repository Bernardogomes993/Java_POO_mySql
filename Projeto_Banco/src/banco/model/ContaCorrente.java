package banco.model;

import banco.exception.SaldoInsuficienteException;
import banco.exception.ValorInvalidoException;

public class ContaCorrente extends Conta implements Tributavel {

    private static final double TAXA_TRANSFERENCIA = 0.50;

    public  ContaCorrente(int agencia , int numero , Cliente titular){

        super(agencia , numero , titular);

    }

    @Override
    public void levantar(double valor) throws SaldoInsuficienteException {
        double valorComTaxa = valor + TAXA_TRANSFERENCIA;
        super.levantar(valorComTaxa);

    }

    @Override
    public double calcularTributo(){
        return this.getSaldo() * 0.01;
    }

    @Override
    public void imprimirExtrato(){
        System.out.printf("[CONTA CORRENTE] Titular: %s | Saldo: %.2f $ %n" ,
                getTitular().getNome() , getSaldo());
    }





}