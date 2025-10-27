package lista.ex4;
import java.util.Random;

/**
 * Esta classe cuida da lógica do jogo de adivinhação para UM cliente.
 * Cada cliente conectado deve ter sua própria instância desta classe.
 */
public class LogicaJogoAdivinhacao {

    private final int numeroSecreto;
    private boolean acertou;

    // Constantes para as respostas do servidor
    public static final String RESPOSTA_MAIOR = "MAIOR";
    public static final String RESPOSTA_MENOR = "MENOR";
    public static final String RESPOSTA_ACERTOU = "ACERTOU!";
    public static final String RESPOSTA_ERRO_FORMATO = "ERRO: Envie apenas numeros.";

    /**
     * No construtor, geramos o número aleatório (ex: 1 a 100)
     * e inicializamos o estado do jogo.
     */
    public LogicaJogoAdivinhacao() {
        // Gera um número aleatório entre 1 e 100 (inclusive)
        this.numeroSecreto = new Random().nextInt(100) + 1;
        this.acertou = false;
        
        // Linha útil para debug (você pode remover depois)
        System.out.println("[Servidor-Logica] Novo jogo iniciado. Numero secreto: " + this.numeroSecreto);
    }

    /**
     * Este é o método principal que a thread do seu cliente chamará.
     * Ele recebe o palpite do cliente e retorna a dica.
     *
     * @param palpiteCliente A string enviada pelo cliente (ex: "50").
     * @return A dica ("MAIOR", "MENOR" ou "ACERTOU!").
     */
    public String verificarPalpite(String palpiteCliente) {
        
        // Se o cliente já acertou, não processa mais
        if (this.acertou) {
            return RESPOSTA_ACERTOU;
        }

        int palpite;

        // 1. Tentar converter a string do cliente para número
        try {
            palpite = Integer.parseInt(palpiteCliente.trim());
        } catch (NumberFormatException e) {
            // Se o cliente enviar "abc" ou algo inválido
            return RESPOSTA_ERRO_FORMATO;
        }

        // 2. Comparar o palpite com o número secreto
        if (palpite == this.numeroSecreto) {
            this.acertou = true;
            return RESPOSTA_ACERTOU;
        } else if (palpite > this.numeroSecreto) {
            return RESPOSTA_MENOR; // O número secreto é MENOR que o palpite
        } else {
            return RESPOSTA_MAIOR; // O número secreto é MAIOR que o palpite
        }
    }

    /**
     * Método auxiliar para a thread do servidor saber quando parar o loop
     * de leitura para este cliente.
     *
     * @return true se o cliente já acertou o número.
     */
    public boolean clienteJaAcertou() {
        return this.acertou;
    }

    public int getNumeroSecreto() {
        return numeroSecreto;
    }
}