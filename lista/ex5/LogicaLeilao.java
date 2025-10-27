package lista.ex5;

/**
 * Esta é a classe de lógica central.
 * APENAS UMA instância dela deve existir no servidor,
 * e todas as threads de cliente devem usá-la.
 */
public class LogicaLeilao {

    private final ItemLeilao item;
    private double maiorLance;
    private String nomeMaiorLicitante;

    // O "lock" é um objeto usado para garantir a sincronização
    private final Object lock = new Object();

    /**
     * O construtor define o item e o lance inicial
     */
    public LogicaLeilao(ItemLeilao item) {
        this.item = item;
        this.maiorLance = item.lanceMinimo;
        this.nomeMaiorLicitante = "Servidor (Lance Inicial)";
    }

    /**
     * Este é o método principal que as threads dos clientes chamarão.
     * É 'synchronized' para ser thread-safe.
     *
     * @param valorDoLance O valor que o cliente está oferecendo.
     * @param nomeLicitante O nome (ou ID) do cliente.
     * @return Um objeto ResultadoLance com a resposta.
     */
    public ResultadoLance fazerLance(double valorDoLance, String nomeLicitante) {
        
        // 'synchronized' garante que apenas uma thread execute este bloco por vez
        synchronized (lock) {
            
            if (valorDoLance <= this.maiorLance) {
                // Lance muito baixo, retorna uma mensagem SÓ para este cliente
                String msgErro = "LANCE_REJEITADO: Seu lance de R$" + valorDoLance + 
                                 " e menor ou igual ao lance atual (R$" + this.maiorLance + ").";
                return new ResultadoLance(false, msgErro);
            
            } else {
                // Lance aceito!
                this.maiorLance = valorDoLance;
                this.nomeMaiorLicitante = nomeLicitante;
                
                // Prepara uma mensagem que o servidor deve enviar A TODOS
                String msgPublica = "NOVO_LANCE: " + nomeLicitante + 
                                    " deu um novo lance de R$" + this.maiorLance + "!";
                                    
                // Retorna o resultado, marcando-o como "publico"
                return new ResultadoLance(true, msgPublica, true);
            }
        } // Fim do bloco synchronized
    }

    /**
     * Método para novos clientes saberem o status atual do leilão
     * assim que se conectarem.
     * @return Uma string com o status.
     */
    public String getStatusAtual() {
        // A leitura também deve ser sincronizada para evitar "leituras sujas"
        synchronized (lock) {
            return "ITEM: " + item.nome + " | MAIOR LANCE: R$" + maiorLance + 
                   " por " + nomeMaiorLicitante;
        }
    }
}