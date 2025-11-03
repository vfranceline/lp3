public class Eleitor {
    private String cpf;
    private String senhaHash;
    private boolean admin;
    
    public Eleitor(String cpf, String senhaHash, boolean admin) {
        this.cpf = cpf;
        this.senhaHash = senhaHash;
        this.admin = admin;
    }
    
    public String getCpf() { 
        return cpf; 
    }
    public String getSenhaHash() { 
        return senhaHash; 
    }
    public boolean isAdmin() { 
        return admin; 
    }
}
