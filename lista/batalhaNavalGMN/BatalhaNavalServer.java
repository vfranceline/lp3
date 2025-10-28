package lista.batalhaNavalGMN;

import java.io.*;
import java.net.*;

public class BatalhaNavalServer {
    
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("[Servidor de Batalha Naval] Aguardando jogadores na porta " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            
            // Loop infinito para parear jogadores
            while (true) {
                // 1. Espera o Jogador 1 (Bloqueia a thread 'main' aqui)
                Socket socketP1 = serverSocket.accept();
                System.out.println("[Servidor] Jogador 1 (P1) conectou: " + socketP1.getInetAddress());
                PrintWriter outP1 = new PrintWriter(socketP1.getOutputStream(), true);
                outP1.println("BEM_VINDO_P1. Você é o Jogador 1. Aguardando oponente...");

                // 2. Espera o Jogador 2 (Bloqueia a thread 'main' aqui de novo)
                Socket socketP2 = serverSocket.accept();
                System.out.println("[Servidor] Jogador 2 (P2) conectou: " + socketP2.getInetAddress());
                PrintWriter outP2 = new PrintWriter(socketP2.getOutputStream(), true);
                outP2.println("BEM_VINDO_P2. Você é o Jogador 2. O jogo vai começar.");
                
                // 3. Cria um NOVO JOGO (recurso compartilhado) SÓ PARA ELES
                System.out.println("[Servidor] Pareando P1 e P2. Iniciando novo jogo.");
                LogicaJogo novoJogo = new LogicaJogo();

                // 4. Cria e inicia as threads para cada jogador
                // Ambas as threads recebem a MESMA instância de 'novoJogo'
                Thread threadP1 = new Thread(new ClientHandler(socketP1, novoJogo, "P1"));
                Thread threadP2 = new Thread(new ClientHandler(socketP2, novoJogo, "P2"));

                threadP1.start();
                threadP2.start();
                
                // A thread 'main' volta ao topo do loop para esperar os próximos 2 jogadores
            }

        } catch (IOException e) {
            System.err.println("Erro fatal no servidor: " + e.getMessage());
        }
    }

    /**
     * ClientHandler é a tarefa (Runnable) que gerencia UM jogador.
     * Ela é executada por uma thread dedicada.
     */
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final LogicaJogo jogo; // O jogo compartilhado (só com o oponente)
        private final String meuId;    // "P1" ou "P2"
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, LogicaJogo jogo, String meuId) {
            this.socket = socket;
            this.jogo = jogo;
            this.meuId = meuId;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // --- FASE 1: SETUP (Simplificada) ---
                // (Aqui você pediria para o cliente posicionar os navios.
                // Para agilizar, a LogicaJogo já começa com navios)
                out.println(jogo.getTabuleiroPara(meuId));
                out.println("Navios posicionados. O jogo começa!");

                // --- FASE 2: LOOP DO JOGO ---
                while (true) {
                    
                    // 1. ESPERAR PELO TURNO
                    // Chama o método 'synchronized' que fará a thread
                    // "dormir" (estado WAITING) até ser acordada
                    out.println("AGUARDANDO_TURNO. (Vez do oponente...)");
                    jogo.esperarMeuTurno(meuId);
                    
                    // 2. É MEU TURNO!
                    out.println("SEU_TURNO. Digite a coordenada do tiro (ex: A5):");
                    String jogada = in.readLine();

                    if (jogada == null || "exit".equalsIgnoreCase(jogada.trim())) {
                        break; // Cliente desconectou ou desistiu
                    }
                    
                    // 3. FAZER A JOGADA
                    // Chama o outro método 'synchronized' para processar o tiro
                    String resultado = jogo.fazerJogada(jogada, meuId);

                    // Envia o resultado da jogada para este cliente
                    out.println("Resultado do seu tiro: " + resultado);
                    
                    // (Opcional) Notifica o oponente sobre o tiro
                    jogo.notificarOponente(meuId, "Oponente atirou em " + jogada + ". Resultado: " + resultado);

                    if (resultado.startsWith("VITORIA")) {
                        out.println("Você venceu!");
                        break;
                    }
                }

            } catch (IOException e) {
                System.out.println("Jogador " + meuId + " desconectou: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("Thread do Jogador " + meuId + " foi interrompida.");
            } finally {
                // Limpeza: fecha o socket
                try {
                    socket.close();
                } catch (IOException ignore) {}
                System.out.println("Conexão com " + meuId + " encerrada.");
                
                // (Opcional) Avisa o outro jogador que este desistiu
                jogo.notificarOponente(meuId, "Oponente desconectou. Você venceu por W.O.");
            }
        }
    }
}
