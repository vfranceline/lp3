package lista.ex6;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Esta é a classe de lógica central da votação.
 * APENAS UMA instância dela deve existir no servidor,
 * e todas as threads de cliente devem usá-la.
 */
public class LogicaVotacao {

    // Armazena a contagem de votos para cada opção
    private final Map<String, Integer> contagemVotos;
    
    // Objeto de Sincronização
    private final Object lock = new Object();

    /**
     * O construtor inicializa as opções de votação.
     */
    public LogicaVotacao() {
        contagemVotos = new HashMap<>();
        // Opções de exemplo
        contagemVotos.put("OPCAO_A", 0);
        contagemVotos.put("OPCAO_B", 0);
        contagemVotos.put("OPCAO_C", 0);
    }

    /**
     * Método para um novo cliente saber quais são as opções.
     * @return Uma string listando as opções.
     */
    public String getOpcoes() {
        // Não precisa de 'synchronized' pois as chaves do mapa (as opções)
        // não mudam depois do construtor.
        return "OPCOES_VALIDAS: " + String.join(", ", contagemVotos.keySet());
    }

    /**
     * Formata os resultados atuais em uma única string.
     * Este método é privado e deve ser chamado DE DENTRO de um bloco
     * 'synchronized' para garantir consistência.
     */
    private String getResultadosFormatados() {
        // Ex: "RESULTADOS: OPCAO_A: 5, OPCAO_B: 3, OPCAO_C: 1"
        return "RESULTADOS_ATUAIS: " + contagemVotos.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    /**
     * Processa um comando recebido do cliente (votar ou pedir resultados)
     * Este método é thread-safe.
     *
     * @param comando O comando enviado pelo cliente (ex: "OPCAO_A" ou "RESULTADOS")
     * @return Um objeto ResultadoVotacao com a resposta.
     */
    public ResultadoVotacao processarComando(String comando) {
        if (comando == null) {
            return new ResultadoVotacao("ERRO: Comando nulo.", false);
        }
        
        String cmd = comando.trim().toUpperCase();

        // O 'synchronized' garante que apenas uma thread
        // por vez possa ler ou modificar o mapa de votos.
        synchronized (lock) {
            
            if (contagemVotos.containsKey(cmd)) {
                // É um VOTO VÁLIDO
                int contagemAtual = contagemVotos.get(cmd);
                contagemVotos.put(cmd, contagemAtual + 1); // Incrementa o voto 
                
                // Retorna os novos resultados como uma notificação PÚBLICA
                String resultados = getResultadosFormatados();
                return new ResultadoVotacao("VOTO_COMPUTADO. " + resultados, true);
            
            } else if (cmd.equals("RESULTADOS")) {
                // É um PEDIDO DE RESULTADOS
                // Retorna os resultados atuais como uma mensagem PRIVADA
                return new ResultadoVotacao(getResultadosFormatados(), false);
            
            } else {
                // É um COMANDO INVÁLIDO
                // Retorna um erro como mensagem PRIVADA
                return new ResultadoVotacao("ERRO: Comando invalido ou opcao inexistente.", false);
            }
        } // Fim do bloco synchronized
    }
}