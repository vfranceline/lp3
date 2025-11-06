package RMI.MockExam;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class ClienteAdministrador {
    private ServicoVotacao servico;
    private String token;
    private Scanner scanner;
    
    public ClienteAdministrador() {
        scanner = new Scanner(System.in);
    }
    
    public void conectar(String host) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            servico = (ServicoVotacao) registry.lookup("ServicoVotacao");
            System.out.println("Conectado ao servidor de votação!");
        } catch (Exception e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void executar() {
        System.out.println("\n========================================");
        System.out.println("  PAINEL DE ADMINISTRAÇÃO - VOTAÇÃO");
        System.out.println("========================================\n");
        
        // Autenticação
        if (!autenticar()) {
            System.out.println("Falha na autenticação. Encerrando...");
            return;
        }
        
        // Menu administrativo
        boolean continuar = true;
        while (continuar) {
            try {
                System.out.println("\n--- MENU ADMINISTRATIVO ---");
                System.out.println("1. Iniciar eleição");
                System.out.println("2. Encerrar eleição");
                System.out.println("3. Ver status da eleição");
                System.out.println("4. Ver resultado parcial");
                System.out.println("5. Ver resultado final");
                System.out.println("6. Listar candidatos");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");
                
                int opcao = scanner.nextInt();
                scanner.nextLine();
                
                switch (opcao) {
                    case 1:
                        iniciarEleicao();
                        break;
                    case 2:
                        encerrarEleicao();
                        break;
                    case 3:
                        verificarStatus();
                        break;
                    case 4:
                        verResultadoParcial();
                        break;
                    case 5:
                        verResultadoFinal();
                        break;
                    case 6:
                        listarCandidatos();
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("Encerrando...");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }
    
    private boolean autenticar() {
        try {
            System.out.println("=== AUTENTICAÇÃO ADMINISTRATIVA ===");
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();
            
            token = servico.autenticar(login, senha, true);
            
            if (token != null) {
                System.out.println("\nAutenticação bem-sucedida!");
                return true;
            } else {
                System.out.println("\nLogin ou senha incorretos!");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            return false;
        }
    }
    
    private void iniciarEleicao() {
        try {
            boolean sucesso = servico.iniciarEleicao(token);
            if (sucesso) {
                System.out.println("\nEleição iniciada com sucesso!");
            } else {
                System.out.println("\nNão foi possível iniciar a eleição.");
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    private void encerrarEleicao() {
        try {
            System.out.print("\nTem certeza que deseja encerrar a eleição? (S/N): ");
            String confirmacao = scanner.nextLine().toUpperCase();
            
            if (confirmacao.equals("S")) {
                boolean sucesso = servico.encerrarEleicao(token);
                if (sucesso) {
                    System.out.println("\nEleição encerrada com sucesso!");
                } else {
                    System.out.println("\nNão foi possível encerrar a eleição.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    private void verificarStatus() {
        try {
            String status = servico.obterStatusEleicao();
            System.out.println("\nStatus: " + status);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    private void verResultadoParcial() {
        try {
            Map<Integer, Integer> resultado = servico.obterResultadoParcial(token);
            exibirResultado(resultado, "RESULTADO PARCIAL");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    private void verResultadoFinal() {
        try {
            Map<Integer, Integer> resultado = servico.obterResultadoFinal(token);
            exibirResultado(resultado, "RESULTADO FINAL");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    private void exibirResultado(Map<Integer, Integer> votos, String titulo) {
        try {
            System.out.println("\n=== " + titulo + " ===");
            var candidatos = servico.listarCandidatos();
            
            int totalVotos = votos.values().stream().mapToInt(Integer::intValue).sum();
            System.out.println("Total de votos: " + totalVotos);
            System.out.println();
            
            for (Candidato c : candidatos) {
                int qtdVotos = votos.getOrDefault(c.getId(), 0);
                double percentual = totalVotos > 0 ? (qtdVotos * 100.0 / totalVotos) : 0;
                System.out.printf("%s: %d votos (%.2f%%)\n", 
                    c.getNome(), qtdVotos, percentual);
            }
        } catch (Exception e) {
            System.err.println("Erro ao exibir resultado: " + e.getMessage());
        }
    }
    
    private void listarCandidatos() {
        try {
            System.out.println("\n=== CANDIDATOS ===");
            var candidatos = servico.listarCandidatos();
            for (Candidato c : candidatos) {
                System.out.println(c);
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        ClienteAdministrador cliente = new ClienteAdministrador();
        
        String host = "localhost";
        if (args.length > 0) {
            host = args[0];
        }
        
        cliente.conectar(host);
        cliente.executar();
    }
}