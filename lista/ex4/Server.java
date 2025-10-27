package lista.ex4;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("[Server] Ouvindo na porta  " + PORT + "...");

        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = server.accept();

                System.out.println("[Server] Conexão de: " + socket.getInetAddress().getHostAddress());

                pool.execute(new ClientHandler(socket));
            }

        } catch (Exception e) {
            System.err.println("[Server] Erro: " + e.getMessage());
            // Se o servidor falhar (ex: porta já em uso), desligamos o pool.
            pool.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;

        ClientHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            LogicaJogoAdivinhacao jogo = new LogicaJogoAdivinhacao();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                out.println("Bem vindo ao jogo de adivinhação! \n Digite seu primeiro palpite ou digite 'exit' para sair.");

                String line;

                while ((line = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(line.trim())) {
                        out.println("Que pena! O numero era: " + jogo.getNumeroSecreto() + ". Até logo!");
                        break; // Quebra o loop
                    }                 
                    String resultado = jogo.verificarPalpite(line);
                    out.println(resultado);

                    if (resultado.equalsIgnoreCase("ACERTOU!")) {
                        out.println("Parabens! Voce venceu! Encerrando o jogo.");
                        break;
                    }
                    
                }
                
                
            } catch (Exception e) {
                // Isso acontece se o cliente fechar a janela (desconexão abrupta).
                System.out.println("Cliente desconectou: " + e.getMessage());
            } finally {             
                try { 
                    socket.close(); // Fecha a conexão (o Socket) com este cliente.
                } catch (IOException ignore) {}
                
            }
        }
    }
}
