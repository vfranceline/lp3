import java.io.Serializable;

public class InfoMotorista implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String motoristaId;
    private String nome;
    private String placa;
    private Localizacao posicaoAtual;
    private Prioridade prioridade;  // ADICIONADO: prioridade do motorista
    private long timestamp;          // ADICIONADO: timestamp de registro
    
    public InfoMotorista(String motoristaId, String nome, String placa, Localizacao posicaoAtual) {
        this.motoristaId = motoristaId;
        this.nome = nome;
        this.placa = placa;
        this.posicaoAtual = posicaoAtual;
        this.prioridade = Prioridade.STANDARD;  // Padrão: STANDARD
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getMotoristaId() { 
        return motoristaId; 
    }

    public String getNome() { 
        return nome; 
    }

    public String getPlaca() { 
        return placa; 
    }

    public Localizacao getPosicaoAtual() { 
        return posicaoAtual; 
    }
    
    public Prioridade getPrioridade() {  // ADICIONADO
        return prioridade;
    }
    
    public long getTimestamp() {  // ADICIONADO
        return timestamp;
    }
    
    public void setPosicaoAtual(Localizacao posicaoAtual) { 
        this.posicaoAtual = posicaoAtual; 
    }
    
    public void setPrioridade(Prioridade prioridade) {  // ADICIONADO
        this.prioridade = prioridade;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s", nome, motoristaId, placa);
    }
}