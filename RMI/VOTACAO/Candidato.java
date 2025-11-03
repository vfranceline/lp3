import java.io.Serializable;

public class Candidato implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nome;
    private String partido;
    private int numero;
    
    public Candidato(int id, String nome, String partido, int numero) {
        this.id = id;
        this.nome = nome;
        this.partido = partido;
        this.numero = numero;
    }
    
    public int getId() { 
        return id; 
    }
    public String getNome() { 
        return nome; 
    }
    public String getPartido() { 
        return partido; 
    }
    public int getNumero() { 
        return numero; 
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s - %s (Número: %d)", id, nome, partido, numero);
    }
}