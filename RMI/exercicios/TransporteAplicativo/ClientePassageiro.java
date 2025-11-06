import java.rmi.*;
import java.rmi.registry.*;

public class ClientePassageiro {
    
    private ServicoDespachante despachante;
    
    public void conectarDespachante() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        despachante = (ServicoDespachante) registry.lookup("DespachanteCorridas");
    }
    
    public BilheteCorrida solicitarCorrida(InfoPassageiro passageiro, Localizacao origem,
                                           Localizacao destino, Prioridade prioridade) 
            throws RemoteException {
        return despachante.solicitarCorrida(passageiro, origem, destino, prioridade);
    }
    
    public BilheteCorrida consultarCorrida(String corridaId) throws RemoteException {
        return despachante.consultarCorrida(corridaId);
    }
    
    public void monitorarCorrida(String corridaId, int segundos) {
        System.out.println("\n⏳ Monitorando corrida por " + segundos + " segundos...\n");
        
        for (int i = 0; i < segundos; i++) {
            try {
                Thread.sleep(1000);
                BilheteCorrida bilhete = consultarCorrida(corridaId);
                System.out.printf("[%ds] Status: %s | Motorista: %s\n", 
                    i + 1, 
                    bilhete.getStatus(), 
                    bilhete.getMotoristaId() != null ? bilhete.getMotoristaId() : "Aguardando...");
                
                if (bilhete.getStatus() != StatusRequisicao.PENDENTE) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("Erro ao consultar: " + e.getMessage());
                break;
            }
        }
    }
    
    public static void main(String[] args) {
        if (args.length < 7) {
            System.out.println("Uso: java ClientePassageiro <id> <nome> <origemLat> <origemLon> <destinoLat> <destinoLon> <prioridade>");
            System.out.println("Exemplo: java ClientePassageiro P001 Ana -23.5505 -46.6333 -23.5600 -46.6400 VIP");
            return;
        }
        
        String id = args[0];
        String nome = args[1];
        double origemLat = Double.parseDouble(args[2]);
        double origemLon = Double.parseDouble(args[3]);
        double destinoLat = Double.parseDouble(args[4]);
        double destinoLon = Double.parseDouble(args[5]);
        Prioridade prioridade = Prioridade.valueOf(args[6].toUpperCase());
        
        try {
            ClientePassageiro cliente = new ClientePassageiro();
            cliente.conectarDespachante();
            
            InfoPassageiro passageiro = new InfoPassageiro(id, nome);
            Localizacao origem = new Localizacao(origemLat, origemLon);
            Localizacao destino = new Localizacao(destinoLat, destinoLon);
            
            System.out.println("\n=== SOLICITAÇÃO DE CORRIDA ===");
            System.out.println("Passageiro: " + nome + " (" + id + ")");
            System.out.println("Origem: " + origem);
            System.out.println("Destino: " + destino);
            System.out.println("Prioridade: " + prioridade);
            
            BilheteCorrida bilhete = cliente.solicitarCorrida(passageiro, origem, destino, prioridade);
            
            System.out.println("\n✓ Corrida solicitada!");
            System.out.println("Bilhete: " + bilhete);
            
            // Monitorar por 5 segundos
            cliente.monitorarCorrida(bilhete.getCorridaId(), 5);
            
            System.out.println("\n=== FIM ===\n");
            
        } catch (Exception e) {
            System.err.println("Erro no cliente passageiro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}