package lista.batalhaNavalGMN;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Esta é a lógica do jogo (o recurso compartilhado).
 * UMA instância desta classe é criada para CADA PAR de jogadores.
 * Todos os métodos-chave são 'synchronized' para serem thread-safe.
 *
 */
public class LogicaJogo {

    // 0 = Água, 1 = Navio, 2 = Água Atingida, 3 = Navio Atingido
    private int[][] tabuleiroP1;
    private int[][] tabuleiroP2;
    private String jogadorDoTurno;
    private int naviosRestantesP1;
    private int naviosRestantesP2;

    // (Opcional) Guarda o "cano de saída" do oponente para podermos notificá-lo
    private Map<String, PrintWriter> canaisDeSaida = new HashMap<>();

    public LogicaJogo() {
        tabuleiroP1 = new int[5][5]; // Tabuleiros 5x5 simplificados
        tabuleiroP2 = new int[5][5];
        jogadorDoTurno = "P1"; // P1 sempre começa
        
        // Posiciona navios de exemplo
        posicionarNaviosExemplo();
        naviosRestantesP1 = 3;
        naviosRestantesP2 = 3;
    }

    // --- MÉTODOS DE SINCRONIZAÇÃO (O CORAÇÃO DA LÓGICA) ---

    /**
     * Método BLOQUEANTE. A thread (jogador) chama isso e "dorme" (WAITING)
     * até que o 'jogadorDoTurno' seja igual ao 'meuId'.
     * * @param meuId "P1" ou "P2"
     */
    public synchronized void esperarMeuTurno(String meuId) throws InterruptedException {
        // O loop 'while' é crucial. Se a thread acordar ("spurious wakeup")
        // e não for seu turno, ela volta a dormir.
        while (!meuId.equals(jogadorDoTurno)) {
            // wait() libera o lock 'synchronized' e coloca a thread
            // no estado WAITING (aguardando)
            wait();
        }
        // Quando acorda e sai do loop, significa que é o turno deste jogador.
        // A thread agora retém o lock 'synchronized' novamente.
    }

    /**
     * Processa o tiro de um jogador.
     * Só pode ser chamada pelo jogador do turno atual.
     * Ao final, troca o turno e ACORDA a outra thread.
     */
    public synchronized String fazerJogada(String jogada, String meuId) {
        // Validação de turno (segurança extra, embora 'esperarMeuTurno' já faça isso)
        if (!meuId.equals(jogadorDoTurno)) {
            return "ERRO: Não é o seu turno.";
        }

        // 1. Traduzir jogada (ex: "A1" -> [0][0])
        int[] coords = parseCoordenada(jogada);
        if (coords == null) {
            return "JOGADA_INVALIDA (ex: A1, B3, E5)";
        }
        int linha = coords[0];
        int col = coords[1];

        // 2. Determinar em qual tabuleiro atirar
        int[][] tabuleiroOponente = (meuId.equals("P1")) ? tabuleiroP2 : tabuleiroP1;
        
        String resultado;
        int celula = tabuleiroOponente[linha][col];

        // 3. Processar o resultado
        if (celula == 1) { // Acertou um navio
            tabuleiroOponente[linha][col] = 3; // Marca como navio atingido
            resultado = "FOGO!";
            // Decrementa a vida do oponente
            if (meuId.equals("P1")) naviosRestantesP2--;
            else naviosRestantesP1--;
        
        } else if (celula == 0) { // Acertou água
            tabuleiroOponente[linha][col] = 2; // Marca como água atingida
            resultado = "ÁGUA.";
        
        } else { // Já atirou aqui (celula == 2 ou 3)
            return "TIRO_REPETIDO. Tente novamente.";
        }

        // 4. Verificar vitória
        if (naviosRestantesP1 == 0 || naviosRestantesP2 == 0) {
            resultado = "VITORIA! Todos os navios oponentes foram afundados!";
            // (Opcional: mudar estado do jogo para "TERMINADO")
        }

        // 5. TROCAR O TURNO (Crucial)
        jogadorDoTurno = (meuId.equals("P1")) ? "P2" : "P1";

        // 6. ACORDAR A OUTRA THREAD (Crucial)
        // notifyAll() acorda TODAS as threads que estavam em wait()
        // neste objeto ('LogicaJogo'). No nosso caso, acorda o oponente.
        notifyAll();

        return resultado;
    }

    // --- MÉTODOS AUXILIARES ---

    // (Opcional) Guarda o canal de saída do jogador para futuras notificações
    public synchronized void registrarCanal(String meuId, PrintWriter out) {
        this.canaisDeSaida.put(meuId, out);
    }
    
    // (Opcional) Envia uma mensagem para o *outro* jogador
    public synchronized void notificarOponente(String meuId, String mensagem) {
        String idOponente = (meuId.equals("P1")) ? "P2" : "P1";
        if (canaisDeSaida.containsKey(idOponente)) {
            canaisDeSaida.get(idOponente).println("NOTIFICACAO: " + mensagem);
        }
    }

    // Lógica interna para posicionar navios
    private void posicionarNaviosExemplo() {
        tabuleiroP1[0][0] = 1; // Navio em A1
        tabuleiroP1[1][2] = 1; // Navio em C2
        tabuleiroP1[3][3] = 1; // Navio em D4
        
        tabuleiroP2[0][4] = 1; // Navio em E1
        tabuleiroP2[2][1] = 1; // Navio em B3
        tabuleiroP2[4][4] = 1; // Navio em E5
    }

    // Lógica interna para traduzir "A1" em [0, 0]
    private int[] parseCoordenada(String coord) {
        try {
            coord = coord.trim().toUpperCase();
            if (coord.length() != 2) return null;
            
            int col = coord.charAt(0) - 'A'; // 'A' -> 0, 'B' -> 1
            int linha = Integer.parseInt(coord.substring(1)) - 1; // "1" -> 0, "2" -> 1

            if (linha < 0 || linha >= 5 || col < 0 || col >= 5) return null;
            
            return new int[]{linha, col};
        } catch (Exception e) {
            return null; // Falha no parse
        }
    }

    // Retorna uma string do tabuleiro para o cliente
    public String getTabuleiroPara(String meuId) {
        // (Lógica para formatar o 'tabuleiroP1' ou 'tabuleiroP2' como string)
        return "TABULEIRO_EXEMPLO: [1,0,0...]\n";
    }
}