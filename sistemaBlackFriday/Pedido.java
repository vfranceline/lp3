package sistemaBlackFriday;

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

    @Override
    public int compareTo(Pedido outroPedido){
        if (this.prioridade.ordinal() < outroPedido.prioridade.ordinal()){
            return -1;
        } else if (this.prioridade.ordinal() == outroPedido.prioridade.ordinal()){
            return Long.compare(this.timestampCriacao, outroPedido.timestampCriacao);
        } else {
            return 1;
        }
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

