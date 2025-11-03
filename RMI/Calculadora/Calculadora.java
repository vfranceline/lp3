
import java.rmi.*;

public interface Calculadora extends Remote{
    int soma(int x, int y) throws RemoteException;
    int subtracao(int x, int y) throws RemoteException;
    int multiplicacao(int x, int y) throws RemoteException;
    double divisao(int x, int y) throws RemoteException;
}
