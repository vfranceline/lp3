package lista.ex3;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;

public class Server {
    // Define a porta em que o servidor ficará "ouvindo"
    private static final int PORT = 12345;
    
    // 1. O RECURSO COMPARTILHADO
    // Declara a variável do dicionário. 
    // Sendo 'static', ela será COMPARTILHADA por todas as threads de clientes.
    private static dicionario dic;
    
    // Esta lista armazena o "cano de saída" de cada cliente.
    // É uma lista "thread-safe" (CopyOnWriteArrayList), ideal para
    // cenários com muitas leituras (como broadcast), que foi copiada
    // do seu projeto de Chat.
    // (Para este exercício de dicionário, ela não é estritamente necessária,
    // já que um cliente não envia mensagens para os outros).
    private static final List<PrintWriter> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        dic = new dicionario();
        System.out.println("[Server] Ouvindo na porta  " + PORT + "...");

        ExecutorService pool = Executors.newFixedThreadPool(10);

        // O try-with-resources garante que o ServerSocket será fechado
        // se o programa falhar.
        try (ServerSocket server = new ServerSocket(PORT)){
            
            // Loop infinito da thread 'main' do servidor.
            // A principal responsabilidade dela é "aceitar" novos clientes.
            while (true) {
                
                // --- PONTO DE BLOQUEIO (ACCEPT) ---
                // A thread 'main' "dorme" aqui (bloqueada em I/O)
                // e fica ESPERANDO até que um novo cliente se conecte.
                // Quando um cliente conecta, o método .accept() retorna
                // o Socket (a "ligação direta") com aquele cliente.
                Socket socket = server.accept();
                
                System.out.println("[Server] Conexão de: " + socket.getInetAddress().getHostAddress());

                // --- DELEGAÇÃO DA TAREFA ---
                // O servidor não atende o cliente aqui. Em vez disso,
                // ele "entrega" o 'socket' para o pool de threads.
                // O 'pool.execute()' coloca a nova "tarefa" (ClientHandler)
                // na fila. Um dos 10 "trabalhadores" (threads) do pool
                // pegará essa tarefa e executará o método 'run()' dela.
                pool.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            System.err.println("[Server] Erro: " + e.getMessage());
            // Se o servidor falhar (ex: porta já em uso), desligamos o pool.
            pool.shutdown();
        }
    }

    // --- TAREFA DO TRABALHADOR (THREAD DO POOL) ---
    // Esta classe implementa Runnable, o que a torna uma "tarefa"
    // que pode ser executada por uma thread de um ExecutorService.
    private static class ClientHandler implements Runnable {
        private final Socket socket; // O socket (conexão) DESTE cliente.
        private PrintWriter out; // O "cano" de saída para ESTE cliente.

        ClientHandler (Socket socket){
            this.socket = socket;
        }
        
        @Override
        public void run() {
            // Este código é executado pela THREAD DO POOL,
            // não pela thread 'main' do servidor.
            
            // O try-with-resources aqui garante que o "cano" de entrada
            // (BufferedReader) será fechado no final.
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                
                // Inicializa o "cano" de saída (para enviar dados AO cliente)
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                
                // Adiciona o "cano" de saída deste cliente à lista thread-safe
                clients.add(out);

                // Envia a mensagem de boas-vindas para o cliente
                out.println("Bem vindo ao dicionario TCP! \n Digite a palavra que você quer saber o significado ou digite 'exit' para sair.");

                // Loop principal de leitura DESTE cliente
                String line;
                
                // --- PONTO DE BLOQUEIO (READLINE) ---
                // A thread do pool "dorme" aqui, esperando o cliente
                // digitar uma palavra e pressionar Enter.
                while ((line = in.readLine()) != null) {
                    
                    // Verifica se o cliente quer sair
                    if ("exit".equalsIgnoreCase(line.trim())) {
                        out.println("Até logo!");
                        break; // Quebra o loop
                    }
                    
                    // --- LÓGICA DE NEGÓCIO ---
                    // 1. Chama o método .buscarSignificado() no objeto 'dic' (COMPARTILHADO).
                    // 2. Envia o resultado (o significado) de volta para o cliente.
                    out.println(dic.buscarSignificado(line));                 
                }

            } catch (IOException e) {
                // Isso acontece se o cliente fechar a janela (desconexão abrupta).
                System.out.println("Cliente desconectou: " + e.getMessage());
            } finally {
                // --- BLOCO DE LIMPEZA (CRUCIAL) ---
                // Este bloco SEMPRE executa, quer o cliente saia com 'exit'
                // ou feche a janela.
                
                if (out != null) {
                    clients.remove(out); // Remove o cliente da lista
                }
                
                try { 
                    socket.close(); // Fecha a conexão (o Socket) com este cliente.
                } catch (IOException ignore) {}
                
                // A thread "morre" (TERMINATED) ao sair do método run(),
                // mas o ExecutorService a manterá viva no pool para
                // ser reutilizada por um próximo cliente.
            }
        }
        
    }
}
