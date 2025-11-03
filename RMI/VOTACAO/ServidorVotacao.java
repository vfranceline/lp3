import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorVotacao extends UnicastRemoteObject implements ServicoVotacao {
    private static final long serialVersionUID = 1L;
    
    // Armazenamento seguro
    private Map<String, Eleitor> eleitores; // CPF -> Eleitor
    private Map<String, SessaoAutenticacao> sessoes; // Token -> Sessão
    private Map<Integer, Candidato> candidatos; // ID -> Candidato
    private Map<Integer, Integer> votos; // ID Candidato -> Quantidade de votos
    private Set<String> cpfsQueVotaram; // CPFs que já votaram
    
    private StatusEleicao status;
    private long inicioEleicao;
    private long fimEleicao;
    
    public ServidorVotacao() throws RemoteException {
        super();
        this.eleitores = new ConcurrentHashMap<>();
        this.sessoes = new ConcurrentHashMap<>();
        this.candidatos = new ConcurrentHashMap<>();
        this.votos = new ConcurrentHashMap<>();
        this.cpfsQueVotaram = Collections.synchronizedSet(new HashSet<>());
        this.status = StatusEleicao.NAO_INICIADA;
        
        inicializarDados();
    }
    
    private void inicializarDados() {
        // Cadastrar eleitores (em produção, viria de banco de dados)
        cadastrarEleitor("111.111.111-11", "senha123", false);
        cadastrarEleitor("222.222.222-22", "senha456", false);
        cadastrarEleitor("333.333.333-33", "senha789", false);
        cadastrarEleitor("admin", "admin123", true); // Administrador
        
        // Cadastrar candidatos
        candidatos.put(1, new Candidato(1, "João Silva", "Partido A", 10));
        candidatos.put(2, new Candidato(2, "Maria Santos", "Partido B", 20));
        candidatos.put(3, new Candidato(3, "Pedro Oliveira", "Partido C", 30));
        candidatos.put(4, new Candidato(4, "Ana Costa", "Partido D", 40));
        
        // Inicializar contagem de votos
        for (int id : candidatos.keySet()) {
            votos.put(id, 0);
        }
    }
    
    private void cadastrarEleitor(String cpf, String senha, boolean admin) {
        String senhaHash = gerarHash(senha);
        eleitores.put(cpf, new Eleitor(cpf, senhaHash, admin));
    }
    
    @Override
    public synchronized String autenticar(String cpf, String senha, boolean admin) throws RemoteException {
        log("Tentativa de autenticação: " + cpf);
        
        Eleitor eleitor = eleitores.get(cpf);
        if (eleitor == null) {
            log("Falha: eleitor não encontrado");
            return null;
        }
        
        String senhaHash = gerarHash(senha);
        if (!eleitor.getSenhaHash().equals(senhaHash)) {
            log("Falha: senha incorreta");
            return null;
        }

        if ((eleitor.isAdmin() == false && admin == true) || (eleitor.isAdmin() == true && admin == false)) {
            log("Falha: Tentativa de autenticação");
            return null;
        }

        // Gerar token de sessão
        String token = UUID.randomUUID().toString();
        SessaoAutenticacao sessao = new SessaoAutenticacao(cpf, eleitor.isAdmin());
        sessoes.put(token, sessao);
        
        log("Autenticação bem-sucedida: " + cpf);
        return token;
    }
    
    @Override
    public boolean validarToken(String token) throws RemoteException {
        SessaoAutenticacao sessao = sessoes.get(token);
        if (sessao == null) return false;
        
        // Verificar se a sessão expirou (60 minutos)
        if (System.currentTimeMillis() - sessao.getTimestamp() > 3600000) {
            sessoes.remove(token);
            return false;
        }
        
        return true;
    }
    
    @Override
    public synchronized boolean registrarVoto(String token, int idCandidato) throws RemoteException {
        // Validar sessão
        SessaoAutenticacao sessao = sessoes.get(token);
        if (sessao == null || !validarToken(token)) {
            log("Voto rejeitado: sessão inválida");
            return false;
        }
        
        // Verificar status da eleição
        if (status != StatusEleicao.EM_ANDAMENTO) {
            log("Voto rejeitado: eleição não está em andamento");
            return false;
        }
        
        // Verificar se já votou
        String cpf = sessao.getCpf();
        if (cpfsQueVotaram.contains(cpf)) {
            log("Voto rejeitado: eleitor já votou - " + cpf);
            return false;
        }
        
        // Validar candidato
        if (!candidatos.containsKey(idCandidato)) {
            log("Voto rejeitado: candidato inválido");
            return false;
        }
        
        // Registrar voto
        votos.put(idCandidato, votos.get(idCandidato) + 1);
        cpfsQueVotaram.add(cpf);
        
        log("Voto registrado com sucesso - Candidato: " + idCandidato);
        return true;
    }
    
    @Override
    public boolean jaVotou(String token) throws RemoteException {
        SessaoAutenticacao sessao = sessoes.get(token);
        if (sessao == null || !validarToken(token)) {
            return false;
        }
        return cpfsQueVotaram.contains(sessao.getCpf());
    }
    
    @Override
    public List<Candidato> listarCandidatos() throws RemoteException {
        return new ArrayList<>(candidatos.values());
    }
    
    @Override
    public Map<Integer, Integer> obterResultadoParcial(String tokenAdmin) throws RemoteException {
        if (!validarAdmin(tokenAdmin)) {
            throw new RemoteException("Acesso negado: apenas administradores");
        }
        
        if (status != StatusEleicao.EM_ANDAMENTO) {
            throw new RemoteException("Eleição não está em andamento");
        }
        
        log("Resultado parcial solicitado por administrador");
        return new HashMap<>(votos);
    }
    
    @Override
    public Map<Integer, Integer> obterResultadoFinal(String tokenAdmin) throws RemoteException {
        if (!validarAdmin(tokenAdmin)) {
            throw new RemoteException("Acesso negado: apenas administradores");
        }
        
        if (status != StatusEleicao.ENCERRADA) {
            throw new RemoteException("Eleição ainda não foi encerrada");
        }
        
        log("Resultado final solicitado por administrador");
        return new HashMap<>(votos);
    }
    
    @Override
    public synchronized boolean iniciarEleicao(String tokenAdmin) throws RemoteException {
        if (!validarAdmin(tokenAdmin)) {
            throw new RemoteException("Acesso negado: apenas administradores");
        }
        
        if (status != StatusEleicao.NAO_INICIADA) {
            return false;
        }
        
        status = StatusEleicao.EM_ANDAMENTO;
        inicioEleicao = System.currentTimeMillis();
        log("=== ELEIÇÃO INICIADA ===");
        return true;
    }
    
    @Override
    public synchronized boolean encerrarEleicao(String tokenAdmin) throws RemoteException {
        if (!validarAdmin(tokenAdmin)) {
            throw new RemoteException("Acesso negado: apenas administradores");
        }
        
        if (status != StatusEleicao.EM_ANDAMENTO) {
            return false;
        }
        
        status = StatusEleicao.ENCERRADA;
        fimEleicao = System.currentTimeMillis();
        log("=== ELEIÇÃO ENCERRADA ===");
        imprimirResultadoFinal();
        return true;
    }
    
    @Override
    public String obterStatusEleicao() throws RemoteException {
        return status.toString();
    }
    
    private boolean validarAdmin(String token) throws RemoteException {
        SessaoAutenticacao sessao = sessoes.get(token);
        return sessao != null && validarToken(token) && sessao.isAdmin();
    }
    
    private void imprimirResultadoFinal() {
        System.out.println("\n========================================");
        System.out.println("       RESULTADO FINAL DA ELEIÇÃO");
        System.out.println("========================================");
        System.out.println("Total de votos: " + cpfsQueVotaram.size());
        System.out.println("\nVotos por candidato:");
        
        votos.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(entry -> {
                Candidato c = candidatos.get(entry.getKey());
                System.out.printf("%s: %d votos (%.2f%%)\n", 
                    c.getNome(), 
                    entry.getValue(),
                    (entry.getValue() * 100.0 / cpfsQueVotaram.size()));
            });
        System.out.println("========================================\n");
    }
    
    private String gerarHash(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void log(String mensagem) {
        System.out.println("[" + new Date() + "] " + mensagem);
    }
}