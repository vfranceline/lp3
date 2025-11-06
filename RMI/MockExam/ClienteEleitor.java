package RMI.MockExam;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class ClienteEleitor {
    private ServicoVotacao servico;
    private String token;
    private Scanner scanner;
    
    public ClienteEleitor() {
        scanner = new Scanner(System.in);
    }
    
    public void conectar(String host) {
        // ... (método permanece igual) ...
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
        // ... (método permanece igual, ele chama autenticar() e votar()) ...
        System.out.println("\n========================================");
        System.out.println("  SISTEMA DE VOTAÇÃO ELETRÔNICA");
        System.out.println("========================================\n");
        if (!autenticar()) {
            System.out.println("Falha na autenticação. Encerrando...");
            return;
        }
        boolean continuar = true;
        while (continuar) {
            try {
                System.out.println("\n--- MENU ---");
                System.out.println("1. Ver candidatos");
                System.out.println("2. Votar");
                System.out.println("3. Verificar se já votou");
                System.out.println("4. Ver status da eleição");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine();
                switch (opcao) {
                    case 1: listarCandidatos(); break;
                    case 2: votar(); break;
                    case 3: verificarVoto(); break;
                    case 4: verificarStatus(); break;
                    case 0: continuar = false; System.out.println("Encerrando..."); break;
                    default: System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }
    
    private boolean autenticar() {
        try {
            System.out.println("=== AUTENTICAÇÃO ===");
            System.out.print("CPF: ");
            String cpf = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();
            
            // ATIVIDADE 4: Implementar Autenticação no Cliente
            // 1. Chamar o método remoto 'servico.autenticar(cpf, senha, false)'.
            // 2. Armazenar o resultado na variável 'token' (da classe).

            this.token = servico.autenticar(cpf, senha, false);
           
            // throw new UnsupportedOperationException("ATIVIDADE 4 - autenticar() no cliente não implementada.");

            if (token != null) {
                System.out.println("\nAutenticação bem-sucedida!");
                return true;
            } else {
                System.out.println("\nCPF ou senha incorretos!");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Erro na autenticação: " + e.getMessage());
            return false;
        }
    }
    
    private void listarCandidatos() {
        // ... (método permanece igual) ...
         try {
            System.out.println("\n=== CANDIDATOS ===");
            List<Candidato> candidatos = servico.listarCandidatos();
            for (Candidato c : candidatos) {
                System.out.println(c);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar candidatos: " + e.getMessage());
        }
    }
    
    private void votar() {
        try {
            // ATIVIDADE 5: Implementar Lógica de Votação no Cliente
            // 1. Chamar o método remoto 'servico.jaVotou(token)'. Se for true, imprimir "Você já votou..." e retornar (return;).
            // 2. Chamar o método remoto 'servico.obterStatusEleicao()'. Se o status NÃO for "EM_ANDAMENTO", imprimir "A eleição não está em andamento..." e retornar.
            // 3. Chamar o método 'listarCandidatos()' (deste cliente) para mostrar as opções.
            // 4. Ler o ID do candidato do scanner (ex: int idCandidato = scanner.nextInt()).
            // 5. Pedir confirmação (S/N).
            // 6. Se confirmado com "S":
            //    - Chamar o método remoto 'servico.registrarVoto(token, idCandidato)'.
            //    - Se o retorno for 'true', imprimir "Voto registrado com sucesso!".
            //    - Se for 'false', imprimir "Não foi possível registrar o voto.".
            // 7. Se a confirmação não for "S", imprimir "Voto cancelado.".
            
            // throw new UnsupportedOperationException("ATIVIDADE 5 - votar() não implementada.");

            if (servico.jaVotou(token)) {
                System.out.println("\nVocê já votou nesta eleição.");
                return;
            }

            if (!servico.obterStatusEleicao().equals("EM_ANDAMENTO")) {
                System.out.println("\nA eleição não está em andamento...");
                return;
            }

            this.listarCandidatos();

            System.out.print("\nDigite o ID do candidato: ");
            int idCandidato = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            System.out.println("Seu voto irá para o candidato " + idCandidato + " digite 'S' para confirmar ou 'N' para cancelar");

            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("s")) {
                if(servico.registrarVoto(token, idCandidato)){
                    System.out.println("\nVoto registrado com sucesso!");
                    return;
                } else {
                    System.out.println("\nNão foi possível registrar o voto.");
                    return;
                }
            } else {
                System.out.println("\nVoto cancelado.");
                return;
            }

            
        } catch (Exception e) {
            System.err.println("Erro ao votar: " + e.getMessage());
            scanner.nextLine();
        }
    }
    
    private void verificarVoto() {
        // ... (método permanece igual) ...
         try {
            boolean votou = servico.jaVotou(token);
            if (votou) {
                System.out.println("\nVocê já votou nesta eleição.");
            } else {
                System.out.println("\nVocê ainda não votou.");
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    private void verificarStatus() {
        // ... (método permanece igual) ...
         try {
            String status = servico.obterStatusEleicao();
            System.out.println("\nStatus da eleição: " + status);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        // ... (método permanece igual) ...
        ClienteEleitor cliente = new ClienteEleitor();
        String host = "localhost";
        if (args.length > 0) {
            host = args[0];
        }
        cliente.conectar(host);
        cliente.executar();
    }
}