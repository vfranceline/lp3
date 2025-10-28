package lista.batalhaNavalGMN;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Cliente de Batalha Naval.
 * Usa DUAS threads, assim como o ChatClient:
 * 1. A thread 'main' (esta) lê do TECLADO e envia jogadas.
 * 2. A thread 'ServerReader' (interna) lê do SERVIDOR e imprime na tela.
 */
public class ClienteBatalhaNaval {

    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Conectando ao servidor em " + HOST + ":" + PORT + "...");

        try (
            Socket socket = new Socket(HOST, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in) // Para ler do teclado
        ) {
            
            // Inicia a thread "Ouvinte" (ServerReader)
            // Esta thread fica 100% do tempo lendo o que o servidor manda
            Thread serverReader = new Thread(() -> {
                try {
                    String msgDoServidor;
                    // Bloqueia aqui esperando mensagens
                    while ((msgDoServidor = in.readLine()) != null) {
                        System.out.println("[Servidor] " + msgDoServidor);
                        
                        if (msgDoServidor.startsWith("VITORIA")) {
                            System.out.println("O JOGO ACABOU!");
                            socket.close(); // Fecha o socket e encerra o cliente
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Servidor desconectou.");
                }
            });
            serverReader.start();

            // Loop da thread 'main'
            // Esta thread fica 100% do tempo lendo o que o USUÁRIO digita
            while (scanner.hasNextLine()) {
                String jogada = scanner.nextLine();
                out.println(jogada); // Envia a jogada para o servidor
                
                if ("exit".equalsIgnoreCase(jogada.trim())) {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.getMessage());
        }
    }
}
