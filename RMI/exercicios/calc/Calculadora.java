package RMI.exercicios.calc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Calculadora extends Remote{
    double soma(double a, double b) throws RemoteException;
    double subtracao(double a, double b) throws RemoteException;
    double multiplicacao(double a, double b) throws RemoteException;
    double divisao(double a, double b) throws RemoteException;
}
