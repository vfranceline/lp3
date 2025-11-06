public class Atribuicao {
    private String atribuicaoId;
    private String corridaId;
    private String motoristaId;
    private long timestampAtribuicao;
    private boolean confirmada;
    private boolean iniciada;
    private boolean concluida;
    
    public Atribuicao(String atribuicaoId, String corridaId, String motoristaId) {
        this.atribuicaoId = atribuicaoId;
        this.corridaId = corridaId;
        this.motoristaId = motoristaId;
        this.timestampAtribuicao = System.currentTimeMillis();
        this.confirmada = false;
        this.iniciada = false;
        this.concluida = false;
    }
    
    public String getAtribuicaoId() { 
        return atribuicaoId; 
    }

    public String getCorridaId() { 
        return corridaId; 
    }

    public String getMotoristaId() { 
        return motoristaId; 
    }

    public long getTimestampAtribuicao() { 
        return timestampAtribuicao; 
    }

    public boolean isConfirmada() { 
        return confirmada; 
    }

    public boolean isIniciada() { 
        return iniciada; 
    }

    public boolean isConcluida() { return concluida; 
    }
    
    public void setConfirmada(boolean confirmada) { 
        this.confirmada = confirmada; 
    }

    public void setIniciada(boolean iniciada) { 
        this.iniciada = iniciada; 
    }

    public void setConcluida(boolean concluida) { 
        this.concluida = concluida; 
    }
}
