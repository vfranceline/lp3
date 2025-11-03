
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculadoraCliente {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            Calculadora stub = (Calculadora) registry.lookup("Calculadora");
            int a = 30;
            int b = 5;
            // System.out.println("Soma: " + stub.soma(a, b));
            // System.out.println("Subtracao: " + stub.subtracao(a, b));
            System.out.println("Multiplicacao: " + stub.multiplicacao(a, b));
            // System.out.println("Divisao: " + stub.divisao(a, b));
        } catch (Exception e) {
        }
    }
}
