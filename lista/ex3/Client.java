package lista.ex3;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Conectando ao host: " + HOST + " na porta: " + PORT);

        // O try-with-resources garante que todos os "canos" e o "socket"
        // serão fechados automaticamente no final do bloco.
        try(
            // 1. O "Telefonema"
            // Tenta se conectar ao ServerSocket na porta e host especificados.
            Socket socket = new Socket(HOST, PORT);
            
            // 2. "Cano" para LER o que o SERVIDOR envia
            BufferedReader serverIN = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 3. "Cano" para ENVIAR coisas para o SERVIDOR
            // (o 'true' liga o 'autoFlush', que envia os dados imediatamente)
            PrintWriter serverOUT = new PrintWriter(socket.getOutputStream(), true);
            // 4. "Cano" para LER o que o USUÁRIO digita no TECLADO
            BufferedReader userIN = new BufferedReader(new InputStreamReader(System.in))
        ) {
            
        // --- CONTROLE DAS THREADS ---
        // Esta flag (bandeira) atômica controla o loop das duas threads.
        // Ela precisa ser Atômica (ou 'volatile') para garantir "visibilidade":
        // Quando a Thread-1 (main) mudar para 'false', a Thread-2 (ServerReader)
        // deve ENXERGAR essa mudança imediatamente e parar também.
        AtomicBoolean running = new AtomicBoolean(true);

        // --- THREAD 2: O "OUVINTE" DO SERVIDOR ---
        // O cliente precisa fazer duas coisas ao mesmo tempo:
        // 1. Esperar pelo teclado (na thread 'main')
        // 2. Esperar por respostas do servidor (nesta thread 'ServerReader')
        // Se não tivéssemos esta thread, o programa ficaria preso 
        // esperando o teclado E NUNCA imprimiria as respostas do servidor.
        Thread ServerReader = new Thread(() -> { // Cria a "tarefa" usando uma Lambda
            try {
                String significado;

                // --- PONTO DE BLOQUEIO (READLINE) ---
                // Esta thread "dorme" aqui, esperando o SERVIDOR
                // enviar uma linha (o significado ou a msg de boas-vindas).
                while ((significado = serverIN.readLine()) != null) {
                    System.out.println(significado); // Imprime a resposta do servidor
                }
            } catch (Exception e) {
                // Acontece se o servidor cair ou o socket fechar.
            } finally {
                // Se o servidor cair, avisa a thread 'main' (do teclado)
                // para parar também.
                running.set(false);
            }
        });
        
        // Inicia o "trabalhador" 2 (o ouvinte)
        ServerReader.start(); 

        // --- THREAD 1: O "LEITOR" DO TECLADO (THREAD 'main') ---
        String userLine; // Variável para guardar o que o usuário digitou

        // O loop principal da thread 'main'
        // Continua rodando enquanto 'running' for 'true'
        // E o usuário não der Ctrl+C (userIN.readLine() != null)
        while (running.get() && (userLine = userIN.readLine()) != null) {
             
             // Envia a palavra que o usuário digitou para o SERVIDOR
             serverOUT.println(userLine);
             
             // Se o usuário digitou "exit", paramos o cliente
             if ("exit".equalsIgnoreCase(userLine.trim())){
                running.set(false); // Sinaliza para a thread 'ServerReader' parar
                break; // Sai do loop da thread 'main'
             }
        }
        
        // (Opcional) Espera até 500ms pela thread 'ServerReader'
        // terminar antes de fechar o programa.
        try { ServerReader.join(500); } catch (InterruptedException ignore) {}

        } catch (IOException e){
            System.out.println("Não foi possível conectar ao servidor: " + e.getMessage());
        }
        
        // O try-with-resources fecha socket, serverIN, serverOUT, e userIN aqui.
        System.out.println("Conexão encerrada.");
    }
}