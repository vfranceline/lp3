package RMI.exercicios.calc;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;


public class ServerCalc extends UnicastRemoteObject implements Calculadora {
    
    public ServerCalc() throws RemoteException{
        super();
    }
    
    public double soma(double a, double b) throws RemoteException{
        return a + b;
    }

    public double subtracao(double a, double b) throws RemoteException{
        return a - b;
    }

    public double multiplicacao(double a, double b) throws RemoteException{
        return a * b;
    }

    public double divisao(double a, double b) throws RemoteException{
        if (b == 0) {
            throw new RemoteException("ERRO: divisão por zero");
        }
        return a / b;
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(12345);
            
            ServerCalc calc = new ServerCalc();

            Naming.rebind("rmi://localhost:12345/CalculadoraService", calc);

            System.out.println("Server pronto");

        } catch (Exception e) {
            System.out.println("ERRO: " + e.toString());
        }
    }
}
