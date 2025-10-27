package lista.ex4;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Conectando ao host: " + HOST + " na porta: " + PORT);
        BufferedReader userIN = new BufferedReader(new InputStreamReader(System.in));

        try(
            Socket socket = new Socket(HOST, PORT);
            BufferedReader serverIN = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOUT = new PrintWriter(socket.getOutputStream(), true);
        ) {
            
        AtomicBoolean running = new AtomicBoolean(true);

        Thread ServerReader = new Thread(() -> { // Cria a "tarefa" usando uma Lambda
            try {
                String resultado;

                while (running.get() && (resultado = serverIN.readLine()) != null) {
                    System.out.println(resultado); // Imprime a resposta do servidor
                    if (resultado.equalsIgnoreCase("ACERTOU!")) {
                        running.set(false);

                        try {
                            System.in.close();
                        } catch (IOException e) { /* ignora */ }

                        break; // Sai do loop da ServerReader
                    }
                    
                }
            } catch (Exception e) {
                // Acontece se o servidor cair ou o socket fechar.
            } finally {
                // Se o servidor cair, avisa a thread 'main' (do teclado)
                // para parar também.
                running.set(false);
            }
        });
        
        ServerReader.start(); 

        String userLine; // Variável para guardar o que o usuário digitou

        try{
            while (running.get() && (userLine = userIN.readLine()) != null) {
                if (!running.get()){
                    break;
                }
                
                serverOUT.println(userLine);
                
                if ("exit".equalsIgnoreCase(userLine.trim())){
                    running.set(false); // Sinaliza para a thread 'ServerReader' parar
                    break; // Sai do loop da thread 'main'
                }
            }
        } catch (IOException e) {
            System.out.println("O servidor encerrou a conexão.");
        }

        try { ServerReader.join(500); } catch (InterruptedException ignore) {}

        } catch (IOException e){
            System.out.println("Não foi possível conectar ao servidor: " + e.getMessage());
        }
        
        try {
            userIN.close();
        } catch (IOException e) { /* ignora */ }

        System.out.println("Conexão encerrada.");
    }
}