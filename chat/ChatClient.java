import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {
    // Endereço do servidor (localhost)
    private static final String HOST = "127.0.0.1";
    // Porta do servidor
    private static final int PORT = 12345;

    public static void main(String[] args) {
        // Mensagem informando tentativa de conexão
        System.out.println("Conectando em " + HOST + ":" + PORT + "...");
        try (
            // Cria o socket e conecta ao servidor
            Socket socket = new Socket(HOST, PORT);
            // Cria leitor para receber mensagens do servidor
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Cria escritor para enviar mensagens ao servidor (auto-flush ativado)
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
            // Cria leitor para ler entrada do usuário (teclado)
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            // Variável atômica para controlar se o cliente está rodando
            AtomicBoolean running = new AtomicBoolean(true);

            // Thread para ler mensagens do servidor e imprimir na tela
            Thread reader = new Thread(() -> {
                try {
                    String line;
                    // Lê linhas do servidor enquanto houver dados
                    while ((line = serverIn.readLine()) != null) {
                        System.out.println(line); // Exibe mensagem recebida
                    }
                } catch (IOException e) {
                    // Se o servidor fechar a conexão, apenas encerra a thread
                } finally {
                    running.set(false); // Marca que o cliente deve parar
                }
            });
            reader.start(); // Inicia a thread de leitura

            // Loop principal: lê do teclado e envia ao servidor
            String userLine;
            while (running.get() && (userLine = userIn.readLine()) != null) {
                // Envia a linha digitada pelo usuário ao servidor
                // TODO [Aluno]: enviar a linha ao servidor
                serverOut.println(userLine);
                // Se o usuário digitar "exit", encerra o loop
                if ("exit".equalsIgnoreCase(userLine.trim())) {
                    running.set(false);
                    break;
                }
            }
            // Aguarda a thread de leitura terminar (com timeout de 500ms)
            try { reader.join(500); } catch (InterruptedException ignore) {}
        } catch (IOException e) {
            // Em caso de erro de I/O, imprime o stack trace
            e.printStackTrace();
        }
    }
}
