package lista.ex5;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList; // Importa a coleção concorrente!
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Classe principal do servidor de chat
public class LeilaoServer {

    // Porta em que o servidor irá escutar conexões
    private static final int PORT = 12345;

    private static ItemLeilao notebook = new ItemLeilao("notebook", 1000);

    private static LogicaLeilao leilao = new LogicaLeilao(notebook);
    private static final List<PrintWriter> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("[Server] Ouvindo na porta  " + PORT + "...");

        ExecutorService pool = Executors.newFixedThreadPool(10);
        
        // Cria o ServerSocket (o "posto de escuta" TCP)
        try (ServerSocket server = new ServerSocket(PORT)) {
            // Loop infinito da thread 'main' do servidor
            while (true) {
                // --- TÓPICO 2: CICLO DE VIDA DE THREAD ---
                // A thread 'main' bloqueia aqui, no estado RUNNABLE (mas
                // "dormindo" em uma chamada de I/O) esperando uma conexão.
                Socket cliente = server.accept(); // Aceita uma nova conexão de cliente
                
                System.out.println("[Server] Conexão de: " + cliente.getInetAddress().getHostAddress());
                
                pool.execute(new ClientHandler(cliente));
                
            }
        } catch (IOException e) {
            System.err.println("[Server] Erro: " + e.getMessage());
            pool.shutdown();
        }
    }

    // --- TÓPICO 4: CRIAÇÃO COM RUNNABLE ---
    // Esta classe interna é a "tarefa" que cada thread de cliente
    // irá executar. Ela implementa a interface 'Runnable'.
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
                out.println("Bem-vindo " + nome + "! Use 'exit' para sair.");
                out.println(leilao.getStatusAtual());
                broadcast("ATENÇÃO " + nome + " está participando do leilão", out);
                
                String welcomeMSG = "[Server] " + nome + " está participando do leilão";
                System.out.println(welcomeMSG);
                broadcast(welcomeMSG, out);

                // 3) Loop principal para ler mensagens DESTE cliente

                out.println("Qual será seu primeiro lance? (digite apenas o valor)");

                String line;
                while ((line = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(line.trim())) {
                        out.println("Você saiu do leilão. Até logo!");
                        break; // Sai do loop
                    }

                    try {
                        double lance = Double.parseDouble(line);

                        ResultadoLance resultadoLance = new ResultadoLance(false, "");
                        resultadoLance = leilao.fazerLance(lance, nome);
                        
                        if (resultadoLance.getEhNotificacaoPublica()){
                            broadcast("[" + nome + "] " + resultadoLance.mensagem, out);
                        } else {
                            out.println(resultadoLance.mensagem);
                        }
                    } catch (NumberFormatException e) {
                        out.println("ERRO: Envie apenas numeros.");
                    }

                    
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