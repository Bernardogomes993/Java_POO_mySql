package banco.model;

import banco.exception.ValorInvalidoException;

public class ContaPoupanca  extends Conta{

    public ContaPoupanca(int agencia , int numero , Cliente titular){

        super(agencia , numero , titular);

    }

    public void renderJuros(double taxaPercentual) throws ValorInvalidoException {

        double rendimento = getSaldo() * (taxaPercentual /100);
        depositar(rendimento);

    }

    @Override
    public void imprimirExtrato(){
        System.out.printf("[CONTA POUPANÇA] Titular: %s | Saldo: %.2f $%n" ,
                getTitular().getNome() , getSaldo());
    }
}