package lista.ex5;

/**
 * Um objeto para representar o resultado de uma tentativa de lance.
 * Isso ajuda seu servidor TCP a saber o que fazer.
 */
public class ResultadoLance {
    
    public final boolean sucesso;
    public final String mensagem; // A mensagem (pública ou privada)
    
    // Flag para o servidor saber se deve notificar todos ou só o cliente que fez o lance
    public final boolean ehNotificacaoPublica; 

    /**
     * Construtor para respostas privadas (ex: erro de lance baixo)
     */
    public ResultadoLance(boolean sucesso, String msg) {
        this.sucesso = sucesso;
        this.mensagem = msg;
        this.ehNotificacaoPublica = false; 
    }
    
    /**
     * Construtor para notificações públicas (um novo lance foi aceito)
     */
    public ResultadoLance(boolean sucesso, String msg, boolean publica) {
         this.sucesso = sucesso;
         this.mensagem = msg;
         this.ehNotificacaoPublica = publica;
    }

    public boolean getEhNotificacaoPublica(){
        return this.ehNotificacaoPublica;
    }
}
