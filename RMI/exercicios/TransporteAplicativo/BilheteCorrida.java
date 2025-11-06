import java.io.Serializable;

public class BilheteCorrida implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String corridaId;
    private StatusRequisicao status;
    private String motoristaId;
    private long timestamp;
    
    public BilheteCorrida(String corridaId, StatusRequisicao status, String motoristaId, long timestamp) {
        this.corridaId = corridaId;
        this.status = status;
        this.motoristaId = motoristaId;
        this.timestamp = timestamp;
    }
    
    public String getCorridaId() { return corridaId; }
    public StatusRequisicao getStatus() { return status; }
    public String getMotoristaId() { return motoristaId; }
    public long getTimestamp() { return timestamp; }
    
    public void setStatus(StatusRequisicao status) { this.status = status; }
    public void setMotoristaId(String motoristaId) { this.motoristaId = motoristaId; }
    
    @Override
    public String toString() {
        return String.format("Bilhete[%s] Status=%s Motorista=%s", 
            corridaId, status, motoristaId != null ? motoristaId : "N/A");
    }
}
