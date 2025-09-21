import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

private static final int PORT = 12345;
// private static String name = "nao";

    private static final List<PrintWriter> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = server.accept();
                System.out.println("[Servidor] Conexão de: " + socket.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("[Servidor] Erro: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                // 1) registrar cliente
                out.println("Digite seu nome:");
                String readLine = in.readLine();
                String name = readLine != null ? readLine.trim() : "Anonimo";
                
                clients.add(out);

                // 2) dar boas-vindas
                out.println("Bem-vindo " + name + "! Digite mensagens. Use 'exit' para sair.");
                broadcast(name + " entrou em", out);
                System.out.println("[servidor] " + name + " entrou no chat.");
                broadcast("[servidor] " + name + " entrou no chat", out);


                // 3) laço principal de leitura
                String line;
                while ((line = in.readLine()) != null) {
                    // TODO [Aluno]: se a linha for "exit", encerrar este cliente graciosamente (remover da lista e fechar socket).
                    // TODO [Aluno]: fazer broadcast da mensagem para TODOS os outros clientes.
                    //   - dica: itere sobre 'clients' e chame println(...)
                    //   - não envie de volta para o próprio 'out' (opcional)
                    if ("exit".equalsIgnoreCase(line.trim())) {
                        clients.remove(out);
                        socket.close();
                        System.out.println("[Servidor] " + name + " saiu do chat.");
                        broadcast(name + "saiu do chat", out);
                        break;
                    }
                    broadcast("[" + name + "] " + line, out);
                }
            } catch (IOException e) {
                // queda de cliente é comum; logar e seguir
                System.out.println("Cliente desconectou: " + e.getMessage());
            } finally {
                // 4) garantir limpeza
                // TODO [Aluno]: remover o PrintWriter deste cliente de 'clients'
                clients.remove(out);
                try { socket.close(); } catch (IOException ignore) {}
            }
        }

        private void broadcast(String msg, PrintWriter sender) {
            // TODO [Aluno]: enviar 'msg' para todos em 'clients'.
            for (PrintWriter pw : clients) {
                if (pw != sender) {
                    pw.println(msg);
                }
            }
        }
    }
}
