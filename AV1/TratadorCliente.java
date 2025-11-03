import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class TratadorCliente implements Runnable {
    private static final AtomicLong geradorIdCliente = new AtomicLong(0);
    
    private final Socket socket;
    private final String idCliente;
    private final PriorityBlockingQueue<RequisicaoReserva> filaRequisicoes;
    private final MapaAssentos mapaAssentos;
    
    public TratadorCliente(Socket socket, PriorityBlockingQueue<RequisicaoReserva> filaRequisicoes, MapaAssentos mapaAssentos) {
        this.socket = socket;
        this.idCliente = "Cliente-" + geradorIdCliente.incrementAndGet();
        this.filaRequisicoes = filaRequisicoes;
        this.mapaAssentos = mapaAssentos;
    }
    
    @Override
    public void run() {
        System.out.printf("[%s] Conectado de %s%n", idCliente, socket.getRemoteSocketAddress());
        
        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            saida.println("Bem-vindo ao Sistema de Reserva de Voo!");
            saida.println("Comandos: RESERVE, STATUS, MAP, QUIT");
            
            String linha;
            while ((linha = entrada.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                
                System.out.printf("[%s] Comando: %s%n", idCliente, linha);
                
                String resposta = processarComando(linha);
                saida.println(resposta);
                
                if (linha.toUpperCase().startsWith("QUIT")) {
                    break;
                }
            }
            
        } catch (IOException e) {
            System.err.printf("[%s] Erro: %s%n", idCliente, e.getMessage());
        } finally {
            fecharSocket();
            System.out.printf("[%s] Desconectado%n", idCliente);
        }
    }
    
    private String processarComando(String comando) {
        String[] partes = comando.split("\\s+");
        String cmd = partes[0].toUpperCase();
        
        try {
            switch (cmd) {
                case "RESERVE":
                    return tratarReserva(partes);
                case "STATUS":
                    return tratarStatus();
                case "MAP":
                case "MAPA":
                    return tratarMapa();
                case "QUIT":
                case "SAIR":
                    return "Até logo!";
                default:
                    return "FAIL COMANDO_INVALIDO Comando desconhecido: " + cmd;
            }
        } catch (Exception e) {
            return "FAIL ERRO_SERVIDOR " + e.getMessage();
        }
    }
    
    private String tratarReserva(String[] partes) {
        /** */
        // throw new UnsupportedOperationException("Método tratarReserva() não implementado - ATIVIDADE 2-III");
        /** *
        REQUISITOS:
            1. Validar número de parâmetros (mínimo 4: RESERVE <nome> <categoria> <assento>)
                - Se inválido, retornar "FAIL SINTAXE_INVALIDA Uso: RESERVE <nome> <categoria> <assento>"
            2. Extrair parâmetros: nome (partes[1 e 2]), categoria (partes[3]), assento (partes[4])
            3. Converter categoria para UPPERCASE
            4. Validar categoria usando Categoria.valueOf():
                - Capturar IllegalArgumentException se categoria inválida
                - Retornar "FAIL CATEGORIA_INVALIDA Categoria inválida: <categoria>"
            5. Criar RequisicaoReserva com: idCliente, nome, categoria, codigoAssento
            6. Adicionar requisição à PriorityBlockingQueue usando offer()
            7. Aguardar resposta do SynchronousQueue com poll() e timeout de 30 segundos
            8. Se timeout (resposta null), retornar "FAIL TIMEOUT Operação expirou"
            9. Se resposta com sucesso, retornar "OK <assento> <codigoReserva>"
            10. Se resposta com falha, retornar "FAIL <codigoErro> <mensagemErro>"
            11. Tratar InterruptedException:
                - Restaurar flag: Thread.currentThread().interrupt()
                - Retornar "FAIL ERRO_SERVIDOR Operação interrompida"
        DICAS:
            - A fila de prioridade ordena automaticamente por categoria
            - O SynchronousQueue bloqueia até que o alocador envie a resposta
            - poll() com timeout evita bloqueio infinito
            - Use try-catch para capturar IllegalArgumentException do valueOf()
            - Use String.format() para formatar respostas
        /** */
        if (partes.length < 4){
            return "FAIL SINTAXE_INVALIDA Uso: RESERVE <nome> <categoria> <assento>";
        }
        String nome = partes[0] + partes[1];
        String cate = partes[3].toUpperCase();
        String assento = partes[4];

        try {
            Categoria categoria = Categoria.valueOf(cate);
            RequisicaoReserva requisicaoReserva = new RequisicaoReserva(this.idCliente, nome, categoria, assento);        
            filaRequisicoes.offer(requisicaoReserva);

            try {
                requisicaoReserva.getFilaResposta().poll(30, TimeUnit.SECONDS);
                return "OK <assento> <codigoReserva>";
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "FAIL ERRO_SERVIDOR Operação interrompida";
            }


        } catch (IllegalArgumentException e) {
            return "FAIL CATEGORIA_INVALIDA Categoria inválida: <categoria>";
        }

    }
    
    private String tratarStatus() {
        int livres = mapaAssentos.getQuantidadeAssentosLivres();
        int total = mapaAssentos.getTotalAssentos();
        double ocupacao = mapaAssentos.getTaxaOcupacao();
        
        return String.format("ASSENTOS %d/%d %.1f%%", livres, total, ocupacao);
    }
    
    private String tratarMapa() {
        return mapaAssentos.gerarMapa();
    }
    
    private void fecharSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.printf("[%s] Erro ao fechar socket: %s%n", idCliente, e.getMessage());
        }
    }
}