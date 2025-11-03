import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
public class CalculadoraServidor implements Calculadora{
    @Override
    public int soma(int x, int y) throws RemoteException{
        return x + y;
    }
    @Override
    public int subtracao(int x, int y) throws RemoteException{
        return x - y;
    }
   /** */
    @Override
    public int multiplicacao(int x, int y) throws RemoteException{
        return x * y;
    }
    /** */
    @Override
    public double divisao(int x, int y) throws RemoteException{
        return (double) x / y;
    }

    public static void main(String[] args) {
        try {
            CalculadoraServidor server = new CalculadoraServidor();
            Calculadora stub = (Calculadora) UnicastRemoteObject.exportObject(server, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("Calculadora", stub);
            System.out.println("Servidor Pronto...");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
