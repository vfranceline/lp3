public class RequisicaoCorrida {
    private String corridaId;
    private InfoPassageiro passageiro;
    private Localizacao origem;
    private Localizacao destino;
    private Prioridade prioridade;
    private StatusRequisicao status;
    private long timestamp;
    private String motoristaAtribuido;
    private String atribuicaoId;
    
    public RequisicaoCorrida(String corridaId, InfoPassageiro passageiro, Localizacao origem, Localizacao destino, Prioridade prioridade) {
        this.corridaId = corridaId;
        this.passageiro = passageiro;
        this.origem = origem;
        this.destino = destino;
        this.prioridade = prioridade;
        this.status = StatusRequisicao.PENDENTE;
        this.timestamp = System.currentTimeMillis();
        this.motoristaAtribuido = null;
        this.atribuicaoId = null;
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

    public StatusRequisicao getStatus() { 
        return status; 
    }

    public long getTimestamp() { return timestamp; 
    }

    public String getMotoristaAtribuido() { 
        return motoristaAtribuido; 
    }

    public String getAtribuicaoId() { 
        return atribuicaoId; 
    }
    
    public void setStatus(StatusRequisicao status) { 
        this.status = status; 
    }

    public void setMotoristaAtribuido(String motoristaId) { 
        this.motoristaAtribuido = motoristaId; 
    }

    public void setAtribuicaoId(String atribuicaoId) { 
        this.atribuicaoId = atribuicaoId; 
    }
    
    public BilheteCorrida toBilhete() {
        return new BilheteCorrida(corridaId, status, motoristaAtribuido, timestamp);
    }
}
