import java.io.Serializable;

public class InfoMotorista implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String motoristaId;
    private String nome;
    private String placa;
    private Localizacao posicaoAtual;
    
    public InfoMotorista(String motoristaId, String nome, String placa, Localizacao posicaoAtual) {
        this.motoristaId = motoristaId;
        this.nome = nome;
        this.placa = placa;
        this.posicaoAtual = posicaoAtual;
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
    
    public void setPosicaoAtual(Localizacao posicaoAtual) { 
        this.posicaoAtual = posicaoAtual; 
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s", nome, motoristaId, placa);
    }
}