package banco.model;

import banco.exception.SaldoInsuficienteException;
import banco.exception.ValorInvalidoException;

public abstract class Conta{

    private final int numero;
    private final int agencia;
    private final Cliente titular;
    private double saldo;

    public Conta(int numero , int agencia , Cliente titular){
        this.numero = numero;
        this.agencia = agencia;
        this.titular = titular;
        this.saldo = 0.0;
    }

    public double getSaldo(){
        return saldo;
    }


    public int getNumero(){
        return numero;
    }

    public int getAgencia(){
        return agencia;
    }

    public Cliente getTitular(){
        return titular;
    }

    public abstract void imprimirExtrato();

    public void depositar(double valor) throws ValorInvalidoException {

        if(valor <= 0){
            throw new ValorInvalidoException("O valor de deposito deve ser positivo e maior que zero");

        }

        this.saldo += valor;
    }

    public void levantar(double valor) throws SaldoInsuficienteException {

        if(this.saldo < valor){
            throw  new SaldoInsuficienteException(this.saldo , valor);
        }

        this.saldo -= valor;

    }

    public void transferir(double valor , Conta transferencia) throws SaldoInsuficienteException,
            ValorInvalidoException {

        this.levantar(valor);
        transferencia.depositar(valor);
    }



}
