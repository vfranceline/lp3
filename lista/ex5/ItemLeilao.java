package lista.ex5;

/**
 * Apenas guarda os dados do item que está sendo leiloado.
 */
public class ItemLeilao {
    public final String nome;
    public final double lanceMinimo;
    
    public ItemLeilao(String nome, double lanceMinimo) {
        this.nome = nome;
        this.lanceMinimo = lanceMinimo;
    }
}