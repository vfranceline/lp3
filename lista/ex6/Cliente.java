package lista.ex6;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean; // Importa a Classe Atômica

public class Cliente {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Conectando em " + HOST + ":" + PORT + "...");
        try (
            // Cria o socket (a "ligação telefônica" para o servidor)
            Socket socket = new Socket(HOST, PORT);
            // "Cano" para ler o que o SERVIDOR envia
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // "Cano" para enviar coisas para o SERVIDOR
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
            // "Cano" para ler o que o USUÁRIO digita no teclado
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            
            AtomicBoolean running = new AtomicBoolean(true);

            Thread reader = new Thread(() -> {
                try {
                    String line;
    
                    while ((line = serverIn.readLine()) != null) {
                        System.out.println(line); // Imprime a msg do servidor
                    }
                } catch (IOException e) {
                    // Acontece se o servidor cair ou o socket fechar.
                } finally {
                    // Se o loop quebrar (servidor caiu),
                    // avisa a 'main' thread para parar também.
                    running.set(false);
                }
            });
            reader.start(); // Inicia a thread 'reader' (NEW -> RUNNABLE)

            // Loop principal (executado pela thread 'main')
            String userLine;
            // O loop continua enquanto 'running' for true
            // E o usuário estiver digitando
            while (running.get() && (userLine = userIn.readLine()) != null) {
                // Envia a linha do usuário para o servidor
                serverOut.println(userLine);
                
                // Se o usuário digitar "exit",
                if ("exit".equalsIgnoreCase(userLine.trim())) {
                    running.set(false);
                    break;
                }
            }
            
            try { reader.join(500); } catch (InterruptedException ignore) {}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
