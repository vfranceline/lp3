package lista.ex6;

/**
 * Objeto que representa o resultado de uma tentativa de voto
 * ou um pedido de resultados.
 */
public class ResultadoVotacao {
    
    public final String mensagem; // A mensagem (pública ou privada)
    
    // Flag para o servidor saber se deve notificar todos ou só o cliente
    public final boolean ehNotificacaoPublica; 

    /**
     * Construtor para respostas privadas 
     * (ex: erro de opção inválida, ou um cliente pedindo o placar)
     */
    public ResultadoVotacao(String msg, boolean publica) {
        this.mensagem = msg;
        this.ehNotificacaoPublica = publica; 
    }
}
