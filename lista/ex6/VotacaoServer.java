package lista.ex6;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;

public class VotacaoServer {
    // Porta em que o servidor irá escutar conexões
    private static final int PORT = 12345;

    private static LogicaVotacao votacao = new LogicaVotacao();
    private static final List<PrintWriter> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("[Server] Ouvindo na porta  " + PORT + "...");

        ExecutorService pool = Executors.newFixedThreadPool(10);
        
        // Cria o ServerSocket (o "posto de escuta" TCP)
        try (ServerSocket server = new ServerSocket(PORT)) {
            // Loop infinito da thread 'main' do servidor
            while (true) {
                Socket cliente = server.accept(); // Aceita uma nova conexão de cliente
                
                System.out.println("[Server] Conexão de: " + cliente.getInetAddress().getHostAddress());
                
                pool.execute(new ClientHandler(cliente));
                
            }
        } catch (IOException e) {
            System.err.println("[Server] Erro: " + e.getMessage());
            pool.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket cliente;
        private PrintWriter out; // "Cano" de saída para ESTE cliente

        ClientHandler(Socket cliente) {
            this.cliente = cliente;
        }

        @Override
        public void run() {
            // Este código é executado pela "Thread do Cliente"
            try (BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));) {
                // Cria o "cano" de saída para este cliente
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream())), true);
                
                // 1) Pegando o nickname
                out.println("Informe seu nome");
                String readLine = in.readLine();
                String nome = readLine.trim();
                
                clients.add(out);

                // 2) Envia mensagem de boas-vindas
                out.println("Bem-vindo " + nome + "! Use 'exit' para sair ou 'RESULTADOS' para saber o estado atual da votação");
                out.println(votacao.getOpcoes());
                
                String welcomeMSG = "[Server] " + nome + " vai votar";
                System.out.println(welcomeMSG);

                // 3) Loop principal para ler mensagens DESTE cliente

                out.println("Para quem vai o seu voto?");

                String line;
                while ((line = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(line.trim())) {
                        out.println("Você saiu do leilão. Até logo!");
                        break; // Sai do loop
                    }

                    ResultadoVotacao resultadoVotacao = votacao.processarComando(line);

                    if (resultadoVotacao.ehNotificacaoPublica){
                        broadcast("[" + nome + "] " + resultadoVotacao.mensagem, out);
                    } 
                    out.println(resultadoVotacao.mensagem);
                    
                }
            } catch (IOException e) {
                System.out.println("Cliente desconectou: " + e.getMessage());
            } finally {
                clients.remove(out);
                
                try { cliente.close(); } catch (IOException ignore) {}
            }
        }

        // Método para enviar uma mensagem para todos os clientes
        private void broadcast(String msg, PrintWriter sender) {
            for (PrintWriter pw : clients) {
                if (pw != sender) {
                    pw.println(msg);
                }
            }
        }
    }
}
