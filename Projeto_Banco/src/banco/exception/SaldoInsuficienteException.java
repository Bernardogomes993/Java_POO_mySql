package banco.exception;

public class SaldoInsuficienteException extends Exception{

    public SaldoInsuficienteException (double valorAtual , double valorRetirado){

        super(String.format("Saldo insuficiente! Saldo Atual: %.2f $ | " +
                "Tentativa de levantamento: %.2f $" , valorAtual , valorRetirado));
    }
}