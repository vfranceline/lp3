import java.io.Serializable;

public class AtribuicaoCorrida implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String atribuicaoId;
    private String corridaId;
    private InfoPassageiro passageiro;
    private Localizacao origem;
    private Localizacao destino;
    private Prioridade prioridade;
    private long timestampAtribuicao;
    
    public AtribuicaoCorrida(String atribuicaoId, String corridaId, InfoPassageiro passageiro,
                             Localizacao origem, Localizacao destino, Prioridade prioridade,
                             long timestampAtribuicao) {
        this.atribuicaoId = atribuicaoId;
        this.corridaId = corridaId;
        this.passageiro = passageiro;
        this.origem = origem;
        this.destino = destino;
        this.prioridade = prioridade;
        this.timestampAtribuicao = timestampAtribuicao;
    }
    
    public String getAtribuicaoId() { 
        return atribuicaoId; 
    }

    public String getCorridaId() { 
        return corridaId; 
    }

    public InfoPassageiro getPassageiro() { 
        return passageiro; 
    }

    public Localizacao getOrigem() { 
        return origem; 
    }

    public Localizacao getDestino() { 
        return destino; 
    }

    public Prioridade getPrioridade() { 
        return prioridade; 
    }

    public long getTimestampAtribuicao() { 
        return timestampAtribuicao; 
    }
    
    @Override
    public String toString() {
        return String.format("Atribuicao[%s] Corrida=%s Passageiro=%s Prioridade=%s",
            atribuicaoId, corridaId, passageiro.getNome(), prioridade);
    }
}