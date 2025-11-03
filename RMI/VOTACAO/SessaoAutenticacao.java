public class SessaoAutenticacao {
    private String cpf;
    private boolean admin;
    private long timestamp;
    
    public SessaoAutenticacao(String cpf, boolean admin) {
        this.cpf = cpf;
        this.admin = admin;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getCpf() { 
        return cpf; 
    }
    public boolean isAdmin() { 
        return admin; 
    }
    public long getTimestamp() { 
        return timestamp; 
    }
}
