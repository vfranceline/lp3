import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList; // Importa a coleção concorrente!

// Classe principal do servidor de chat
public class ChatServer {

    // Porta em que o servidor irá escutar conexões
    private static final int PORT = 12345;

    // --- TÓPICO 7: COLEÇÕES PARA CONCORRÊNCIA ---
    // Esta é a lista de todos os clientes conectados.
    // Ela precisa ser "thread-safe" porque várias threads (ClientHandlers)
    // vão acessá-la ao mesmo tempo:
    // - Para ADICIONAR um novo cliente (quando um entra).
    // - Para REMOVER um cliente (quando um sai).
    // - Para ITERAR (percorrer) a lista (para fazer o broadcast).
    //
    // 'CopyOnWriteArrayList' é uma escolha de implementação específica.
    // Ela é OTIMIZADA para cenários onde LEITURAS (iteração)
    // são MUITO mais frequentes que ESCRITAS (add/remove).
    // É o caso do chat: 1 'add'/'remove' por cliente, mas 1 iteração
    // a cada mensagem enviada.
    //
    // Como funciona:
    // - Leituras (broadcast): São rápidas, não usam locks.
    // - Escritas (add/remove): São CARAS. A lista inteira é COPIADA
    //   para um novo array com a modificação.
    //
    // --- TÓPICO 6: SINCRONIZAR COLEÇÕES (A Alternativa) ---
    // Poderíamos ter usado a forma antiga:
    // List<PrintWriter> clients = Collections.synchronizedList(new ArrayList<>());
    // A desvantagem é que *todas* as operações (add, remove, e iteração)
    // usariam um 'synchronized' (Tópico 5), criando um gargalo.
    private static final List<PrintWriter> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("[Server] Ouvindo na porta  " + PORT + "...");
        // Cria o ServerSocket (o "posto de escuta" TCP)
        try (ServerSocket server = new ServerSocket(PORT)) {
            // Loop infinito da thread 'main' do servidor
            while (true) {
                // --- TÓPICO 2: CICLO DE VIDA DE THREAD ---
                // A thread 'main' bloqueia aqui, no estado RUNNABLE (mas
                // "dormindo" em uma chamada de I/O) esperando uma conexão.
                Socket socket = server.accept(); // Aceita uma nova conexão de cliente
                
                System.out.println("[Server] Conexão de: " + socket.getInetAddress().getHostAddress());
                
                // --- TÓPICO 1 (Conceituação) & TÓPICO 4 (Runnable) ---
                // Esta é a essência do servidor multithreaded.
                // Em vez de lidar com o cliente aqui (como no serverTCP.java),
                // o servidor "delega" o cliente para um novo "trabalhador" (Thread).
                //
                // 1. Cria uma "tarefa" (Runnable): new ClientHandler(socket)
                // 2. Cria um "trabalhador" (Thread): new Thread(...)
                // 3. Inicia o trabalhador: .start()
                //
                // Isso move a nova thread para o estado NEW e depois RUNNABLE.
                new Thread(new ClientHandler(socket)).start();
                
                // --- TÓPICO 10/11 (Executores) ---
                // O PDF da atividade sugere usar um "ExecutorService"
                // (Pool de Threads) em vez de 'new Thread().start()'.
                // Por quê? Criar e destruir threads é caro.
                // Um Executor reutiliza threads, o que é muito mais eficiente.
                // O código *poderia* ser:
                //   ExecutorService pool = Executors.newFixedThreadPool(10);
                //   pool.execute(new ClientHandler(socket));
                // Mas a implementação atual usa a forma manual.
            }
        } catch (IOException e) {
            System.err.println("[Server] Erro: " + e.getMessage());
        }
    }

    // --- TÓPICO 4: CRIAÇÃO COM RUNNABLE ---
    // Esta classe interna é a "tarefa" que cada thread de cliente
    // irá executar. Ela implementa a interface 'Runnable'.
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out; // "Cano" de saída para ESTE cliente

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // Este código é executado pela "Thread do Cliente"
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                // Cria o "cano" de saída para este cliente
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                
                // 1) Pegando o nickname
                out.println("Informe seu nickname");
                String readLine = in.readLine();
                String nickname = readLine.trim();
                
                // --- TÓPICO 7 (Coleções para Concorrência) ---
                // ESCRITA na lista thread-safe.
                // Esta operação é cara (copia o array), mas só acontece
                // uma vez por cliente.
                clients.add(out);

                // 2) Envia mensagem de boas-vindas
                out.println("Bem-vindo " + nickname + "! Digite mensagens. Use 'exit' para sair.");
                broadcast("ATENÇÃO " + nickname + " ENTROU NO CHAT", out);
                
                String welcomeMSG = "[Server] " + nickname + " entrou no chat";
                System.out.println(welcomeMSG);
                broadcast(welcomeMSG, out);

                // 3) Loop principal para ler mensagens DESTE cliente
                String line;
                // --- TÓPICO 2 (Ciclo de Vida) ---
                // A thread do cliente bloqueia aqui (I/O wait)
                // esperando que o cliente digite algo.
                while ((line = in.readLine()) != null) {
                    
                    if ("exit".equalsIgnoreCase(line.trim())) {
                        out.println("Você saiu do chat. Até logo!");
                        break; // Sai do loop
                    }
                    
                    // Envia a mensagem recebida para todos os *outros* clientes
                    broadcast("[" + nickname + "] " + line, out);
                }
            } catch (IOException e) {
                System.out.println("Cliente desconectou: " + e.getMessage());
            } finally {
                // 4) Limpeza (acontece se o loop quebrar ou se der erro)
                
                // --- TÓPICO 7 (Coleções para Concorrência) ---
                // ESCRITA na lista thread-safe.
                // Remove o cliente da lista de broadcast.
                // Esta também é uma operação cara (copia o array).
                clients.remove(out);
                
                try { socket.close(); } catch (IOException ignore) {}
                
                // A thread morre (estado TERMINATED) ao sair do método run().
            }
        }

        // Método para enviar uma mensagem para todos os clientes
        private void broadcast(String msg, PrintWriter sender) {
            // --- TÓPICO 7 (Coleções para Concorrência) ---
            // LEITURA da lista thread-safe.
            // Em 'CopyOnWriteArrayList', esta iteração é MUITO RÁPIDA
            // e não usa locks. Ela percorre um "snapshot" (foto)
            // da lista como ela era no momento em que o 'for' começou.
            for (PrintWriter pw : clients) {
                // Não envia a mensagem de volta para quem a enviou
                if (pw != sender) {
                    pw.println(msg);
                }
            }
        }
    }
}