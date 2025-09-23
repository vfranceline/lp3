import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

// Classe principal do servidor de chat
public class ChatServer {

    // Porta em que o servidor irá escutar conexões
    private static final int PORT = 12345;

    // Lista thread-safe para armazenar os clientes conectados (PrintWriter de cada cliente)
    private static final List<PrintWriter> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("[Server] Ouvindo na porta  " + PORT + "...");
        // Cria o ServerSocket e aguarda conexões de clientes
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                // Aceita uma nova conexão de cliente
                Socket socket = server.accept();
                System.out.println("[Server] Conexão de: " + socket.getInetAddress().getHostAddress());
                // Cria uma nova thread para lidar com o cliente conectado
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("[Server] Erro: " + e.getMessage());
        }
    }

    // Classe interna para tratar cada cliente conectado
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // Usa try-with-resources para garantir fechamento do BufferedReader
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                // Cria PrintWriter para enviar mensagens ao cliente
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                // 1) Adiciona o PrintWriter deste cliente à lista de clientes
                //pegando o nick do cliente
                out.println("Informe seu nickname");
                String readLine = in.readLine();
                String nickname = readLine.trim();
                clients.add(out);

                // 2) Envia mensagem de boas-vindas ao cliente
                out.println("Bem-vindo " + nickname + "! Digite mensagens. Use 'exit' para sair.");
                broadcast("ATENÇÃO " + nickname + " ENTROU NO CHAT", out);

                //avisando aos clientes q tão conectados e printando no server tbm
                String welcomeMSG = "[Server] " + nickname + " entrou no chat";
                System.out.println(welcomeMSG);
                broadcast(welcomeMSG, out);

                // 3) Loop principal para ler mensagens do cliente
                String line;
                while ((line = in.readLine()) != null) {
                    // TODO [Aluno]: se a linha for "exit", encerrar este cliente graciosamente (remover da lista e fechar socket).
                    // TODO [Aluno]: fazer broadcast da mensagem para TODOS os outros clientes.
                    //   - dica: itere sobre 'clients' e chame println(...)
                    //   - não envie de volta para o próprio 'out' (opcional)
                    if ("exit".equalsIgnoreCase(line.trim())) {
                        out.println("Você saiu do chat. Até logo!");
                        clients.remove(out); //out é o cliente
                        socket.close();
                        //avisando a galera q ele saiu do chat
                        String goodbyeMSG = "[Server] " + nickname + " saiu do chat!";
                        System.out.println(goodbyeMSG);
                        broadcast(goodbyeMSG, out);
                        break;
                    }
                    // Envia a mensagem recebida para todos os outros clientes conectados
                    broadcast("[" + nickname + "] " + line, out);
                }
            } catch (IOException e) {
                // Se o cliente cair, apenas loga e continua
                System.out.println("Cliente desconectou: " + e.getMessage());
            } finally {
                // 4) Remove o PrintWriter deste cliente da lista de clientes
                // TODO [Aluno]: remover o PrintWriter deste cliente de 'clients'
                clients.remove(out);
                try { socket.close(); } catch (IOException ignore) {}
            }
        }

        // Método para enviar uma mensagem para todos os clientes, exceto o remetente
        private void broadcast(String msg, PrintWriter sender) {
            for (PrintWriter pw : clients) {
                if (pw != sender) {
                    pw.println(msg);
                }
            }
        }
    }
}
