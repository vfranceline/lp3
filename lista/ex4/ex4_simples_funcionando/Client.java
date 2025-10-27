package lista.ex4.ex4_simples_funcionando;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Conectando ao host: " + HOST + " na porta: " + PORT);
        Scanner scanner = new Scanner(System.in);

        try(
            Socket client = new Socket(HOST, PORT);
            ObjectOutputStream numSaida = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream numEntrada = new ObjectInputStream(client.getInputStream());
        ) 
        {
            while (true) { 
                try {
                    System.out.println("Qual seu palpite? ");
                    int numDigitado = scanner.nextInt();
                    scanner.nextLine();
                    
                    numSaida.writeObject(numDigitado);
                    
                    String resultado = (String)numEntrada.readObject();

                    System.out.println(resultado);

                    if (resultado.startsWith("Parabens")) {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Cliente desconectado: " + client.getInetAddress().getHostAddress());
                    break;
                }
            }
        
        } catch (IOException e) {
            System.out.println("O servidor encerrou a conexão.");
        }
    }
}