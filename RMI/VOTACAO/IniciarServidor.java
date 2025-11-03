import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class IniciarServidor {
    public static void main(String[] args) {
        try {
            // Criar o servidor de votação
            ServidorVotacao servidor = new ServidorVotacao();
            
            // Criar o registro RMI na porta 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Registrar o serviço
            registry.rebind("ServicoVotacao", servidor);
            
            System.out.println("========================================");
            System.out.println("  SERVIDOR DE VOTAÇÃO ELETRÔNICA");
            System.out.println("========================================");
            System.out.println("Servidor iniciado na porta 1099");
            System.out.println("Aguardando conexões...\n");
            System.out.println("ELEITORES CADASTRADOS:");
            System.out.println("CPF: 111.111.111-11, Senha: senha123");
            System.out.println("CPF: 222.222.222-22, Senha: senha456");
            System.out.println("CPF: 333.333.333-33, Senha: senha789");
            System.out.println("\nADMINISTRADOR:");
            System.out.println("CPF: admin, Senha: admin123");
            System.out.println("========================================\n");
            
        } catch (Exception e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}