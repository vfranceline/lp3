package lista.ex4.ex4_simples_funcionando;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import lista.ex2.client;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("[Server] Ouvindo na porta  " + PORT + "...");

        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket client = server.accept();

                System.out.println("[Server] Conexão de: " + client.getInetAddress().getHostAddress());

                pool.execute(new ClientHandler(client));
            }

        } catch (Exception e) {
            System.err.println("[Server] Erro: " + e.getMessage());
            // Se o servidor falhar (ex: porta já em uso), desligamos o pool.
            pool.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket client;

        ClientHandler(Socket client){
            this.client = client;
        }

        @Override
        public void run() {
            Random random = new Random();
            int numero = random.nextInt(100);

            try {
                ObjectOutputStream numSaida = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream numEntrada = new ObjectInputStream(client.getInputStream());

                while (true) {
                    try {
                        int numDigitado = (int)numEntrada.readObject();

                        if (numDigitado == numero) {
                            numSaida.writeObject("Parabens! Voce venceu! Encerrando o jogo.");
                            numSaida.flush();
                            break;
                        } else if (numDigitado > numero) {
                            numSaida.writeObject("O número secreto é MENOR que o palpite");
                            numSaida.flush();
                        } else {
                            numSaida.writeObject("O número secreto é MAIOR que o palpite");
                            numSaida.flush();
                        }
                    } catch (Exception e) {
                        System.out.println("Cliente desconectou: " + e.getMessage());
                        break;
                    }                                
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
