import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean; // Importa a Classe Atômica

public class ChatClient {
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
            // --- TÓPICO 8: CLASSES ATÔMICAS ---
            // Esta variável 'running' será acessada por DUAS threads:
            // 1. A thread 'main' (que lê o teclado)
            // 2. A thread 'reader' (que lê do servidor)
            //
            // --- TÓPICO 9: VOLATILE ---
            // Precisamos garantir "visibilidade". Quando a 'main'
            // thread (ao ler "exit") mudar 'running' para 'false',
            // a 'reader' thread TEM QUE VER essa mudança.
            //
            // Poderíamos usar 'private static volatile boolean running = true;'.
            // 'AtomicBoolean' nos dá a mesma garantia de VISIBILIDADE
            // que 'volatile' daria, com a vantagem de também
            // fornecer operações atômicas (como 'compareAndSet').
            AtomicBoolean running = new AtomicBoolean(true);

            // --- TÓPICO 1 (Conceituação) & TÓPICO 3 (Criação com Lambda) ---
            // O cliente precisa de 2 threads para duas tarefas simultâneas:
            // 1. A thread 'main' (abaixo) vai ler do teclado.
            // 2. Esta nova thread, 'reader', vai ler do servidor.
            //
            // Se não tivéssemos esta thread, a 'main' ficaria "presa"
            // em 'userIn.readLine()' e NUNCA veria as mensagens
            // chegando do servidor.
            //
            // Esta é a forma moderna de criar uma thread (Tópico 3):
            // () -> { ... } é uma "expressão lambda", uma forma
            // concisa de implementar a interface 'Runnable' (Tópico 4).
            Thread reader = new Thread(() -> {
                try {
                    String line;
                    // --- TÓPICO 2 (Ciclo de Vida) ---
                    // A thread 'reader' bloqueia aqui (I/O wait)
                    // esperando por mensagens do servidor.
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
                    // --- TÓPICO 8 (Classes Atômicas) ---
                    // A 'main' thread atualiza a flag atômica.
                    // Devido à garantia de "visibilidade", a 'reader'
                    // thread (que está em 'running.get()') verá
                    // esta mudança e vai parar.
                    running.set(false);
                    break;
                }
            }
            
            // --- TÓPICO 2 (Ciclo de Vida) ---
            // A 'main' thread espera a 'reader' thread morrer.
            // .join(500) faz a 'main' thread entrar em "TIMED_WAITING"
            // por até 500ms, esperando a 'reader' terminar.
            try { reader.join(500); } catch (InterruptedException ignore) {}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}