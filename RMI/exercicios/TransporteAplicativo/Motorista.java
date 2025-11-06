public class Motorista {
    private InfoMotorista info;
    private StatusMotorista status;
    private CallbackMotorista callback;
    private String atribuicaoAtual;
    
    public Motorista(InfoMotorista info, CallbackMotorista callback) {
        this.info = info;
        this.status = StatusMotorista.DISPONIVEL;
        this.callback = callback;
        this.atribuicaoAtual = null;
    }
    
    public InfoMotorista getInfo() { 
        return info; 
    }

    public StatusMotorista getStatus() { 
        return status; 
    }

    public CallbackMotorista getCallback() { 
        return callback; 
    }

    public String getAtribuicaoAtual() { 
        return atribuicaoAtual; 
    }
    
    public void setStatus(StatusMotorista status) { 
        this.status = status; 
    }

    public void setAtribuicaoAtual(String atribuicaoId) { 
        this.atribuicaoAtual = atribuicaoId; 
    }
    
    public boolean isDisponivel() {
        return status == StatusMotorista.DISPONIVEL && atribuicaoAtual == null;
    }
}
