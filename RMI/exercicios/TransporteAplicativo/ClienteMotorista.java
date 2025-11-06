import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

public class ClienteMotorista extends UnicastRemoteObject implements CallbackMotorista {
    
    private String motoristaId;
    private String nome;
    private ServicoDespachante despachante;
    
    public ClienteMotorista(String motoristaId, String nome) throws RemoteException {
        super();
        this.motoristaId = motoristaId;
        this.nome = nome;
    }
    
    @Override
    public void aoAtribuir(AtribuicaoCorrida atribuicao) throws RemoteException {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║     🚗 NOVA CORRIDA ATRIBUÍDA!              ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("Atribuição ID: " + atribuicao.getAtribuicaoId());
        System.out.println("Corrida ID: " + atribuicao.getCorridaId());
        System.out.println("Passageiro: " + atribuicao.getPassageiro().getNome());
        System.out.println("Prioridade: " + atribuicao.getPrioridade());
        System.out.println("Origem: " + atribuicao.getOrigem());
        System.out.println("Destino: " + atribuicao.getDestino());
        System.out.println("------------------------------------------");
        
        // Auto-aceitar após 500ms (simulação)
        new Thread(() -> {
            try {
                Thread.sleep(500);
                boolean aceito = despachante.aceitarAtribuicao(atribuicao.getAtribuicaoId());
                if (aceito) {
                    System.out.println("✓ Atribuição aceita: " + atribuicao.getAtribuicaoId());
                    
                    // Simular início após 1s
                    Thread.sleep(1000);
                    despachante.iniciarCorrida(atribuicao.getAtribuicaoId());
                    System.out.println("✓ Corrida iniciada");
                    
                    // Simular conclusão após 3s
                    Thread.sleep(3000);
                    despachante.concluirCorrida(atribuicao.getAtribuicaoId());
                    System.out.println("✓ Corrida concluída - Motorista disponível novamente\n");
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar atribuição: " + e.getMessage());
            }
        }).start();
    }
    
    @Override
    public void aoCancelar(String atribuicaoId) throws RemoteException {
        System.out.println("\n⚠ Corrida cancelada pelo passageiro: " + atribuicaoId);
    }
    
    public void conectarDespachante() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        despachante = (ServicoDespachante) registry.lookup("DespachanteCorridas");
    }
    
    public void registrar(InfoMotorista info) throws RemoteException {
        boolean sucesso = despachante.registrarMotorista(info, this);
        if (sucesso) {
            System.out.println("✓ Motorista registrado com sucesso!");
        } else {
            System.out.println("✗ Falha no registro (ID já existe)");
        }
    }
    
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Uso: java ClienteMotorista <id> <nome> <placa> <latitude> <longitude>");
            System.out.println("Exemplo: java ClienteMotorista M001 João ABC1234 -23.5505 -46.6333");
            return;
        }
        
        String id = args[0];
        String nome = args[1];
        String placa = args[2];
        double lat = Double.parseDouble(args[3]);
        double lon = Double.parseDouble(args[4]);
        
        try {
            ClienteMotorista cliente = new ClienteMotorista(id, nome);
            cliente.conectarDespachante();
            
            InfoMotorista info = new InfoMotorista(id, nome, placa, new Localizacao(lat, lon));
            cliente.registrar(info);
            
            System.out.println("\n=== MOTORISTA ATIVO ===");
            System.out.println("ID: " + id);
            System.out.println("Nome: " + nome);
            System.out.println("Placa: " + placa);
            System.out.println("Posição: " + lat + ", " + lon);
            System.out.println("Status: DISPONÍVEL");
            System.out.println("\nAguardando corridas...\n");
            
            // Manter ativo
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("Erro no cliente motorista: " + e.getMessage());
            e.printStackTrace();
        }
    }
}