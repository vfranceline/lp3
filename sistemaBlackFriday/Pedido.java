package sistemaBlackFriday;

import java.time.*;
import java.time.format.*;

// --- TÓPICO 7: COLEÇÕES PARA CONCORRÊNCIA (FILAS) ---
// Para que a 'PriorityBlockingQueue' (a fila) saiba como
// ordenar os pedidos, a classe Pedido DEVE implementar 'Comparable'.
public class Pedido implements Comparable<Pedido>{
    public enum Prioridade{
        ALTA, MEDIA, BAIXA
    }

    private int idPedido;
    private String nomeCliente;
    private String produto;
    private int quantidade;
    private Prioridade prioridade;
    private long timestampCriacao;
    
    public Pedido(int idPedido, String nomeCliente, String produto, int quantidade, Prioridade prioridade, long timestampCriacao){
        this.idPedido = idPedido;
        this.nomeCliente = nomeCliente;
        this.produto = produto;
        this.quantidade = quantidade;
        this.prioridade = prioridade;
        this.timestampCriacao = timestampCriacao;
    }

    // Esta é a lógica de ordenação da Fila de Prioridade:
    @Override
    public int compareTo(Pedido outroPedido){
        // 1. Compara pela Prioridade (ALTA vem primeiro)
        if (this.prioridade.ordinal() < outroPedido.prioridade.ordinal()){
            return -1; // 'this' é mais prioritário
        } else if (this.prioridade.ordinal() == outroPedido.prioridade.ordinal()){
            // 2. Se a prioridade for igual, desempata pelo tempo (FIFO)
            // O pedido mais ANTIGO (menor timestamp) vem primeiro.
            return Long.compare(this.timestampCriacao, outroPedido.timestampCriacao);
        } else {
            return 1; // 'this' é menos prioritário
        }
    }

    @Override
    public String toString() {
        // Pega o long e converte para um objeto de tempo
        Instant instant = Instant.ofEpochMilli(this.timestampCriacao);
        
        // Define o fuso horário (usei o de São Paulo como padrão) e o formato
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
                                                    .withZone(ZoneId.of("America/Sao_Paulo"));

        // Formata o pedido
        return String.format("Pedido#%d [%s] %s - %s x%d (%s)",
                this.idPedido,
                this.prioridade,
                this.nomeCliente,
                this.produto,
                this.quantidade,
                formatter.format(instant) // Formata o tempo
        );
    }

    public int getIdPedido() {
        return idPedido;
    }
    public String getNomeCliente() {
        return nomeCliente;
    }
    public Prioridade getPrioridade() {
        return prioridade;
    }
    public String getProduto() {
        return produto;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public long getTimestampCriacao() {
        return timestampCriacao;
    }

}

