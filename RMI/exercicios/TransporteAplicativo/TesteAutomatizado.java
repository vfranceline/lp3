import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

public class TesteAutomatizado {
    
    private static final String REGISTRY_HOST = "localhost";
    private static final int REGISTRY_PORT = 1099;
    private static Registry registry;
    private static ServidorDespachante servidor;
    
    // Cores para output (ANSI)
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    
    public static void main(String[] args) {
        System.out.println("\n" + CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println(CYAN + "   TESTE AUTOMATIZADO - SISTEMA DE DESPACHO DE CORRIDAS   " + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════" + RESET + "\n");
        
        try {
            inicializarServidor();
            Thread.sleep(1000);
            
            List<ClienteMotoristaTest> motoristas = registrarMotoristas();
            Thread.sleep(1000);
            
            testeCenario1_MatchingBasico();
            Thread.sleep(3000);
            
            testeCenario2_PrioridadeVIP();
            Thread.sleep(3000);
            
            testeCenario3_Concorrencia();
            Thread.sleep(5000);
            
            testeCenario4_TimeoutConfirmacao(motoristas);
            Thread.sleep(5000);
            
            testeCenario5_Cancelamento();
            Thread.sleep(3000);
            
            testeCenario6_TimeoutMatching();
            Thread.sleep(4000);
            
            testeCenario7_Stress();
            Thread.sleep(10000);
            
            System.out.println("\n" + YELLOW + "Pressione ENTER para finalizar o teste..." + RESET);
            System.in.read();
            
            finalizarTeste();
            
        } catch (Exception e) {
            erro("Erro no teste: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inicializarServidor() throws Exception {
        secao("FASE 1: Inicializando Servidor");
        
        try {
            registry = LocateRegistry.createRegistry(REGISTRY_PORT);
            sucesso("RMI Registry criado na porta " + REGISTRY_PORT);
            
            servidor = new ServidorDespachante();
            registry.rebind("DespachanteCorridas", servidor);
            sucesso("Servidor Despachante registrado");
            
            info("Servidor pronto para receber conexões");
            
        } catch (Exception e) {
            erro("Falha ao inicializar servidor: " + e.getMessage());
            throw e;
        }
    }
    
    private static List<ClienteMotoristaTest> registrarMotoristas() throws Exception {
        secao("FASE 2: Registrando Motoristas");
        
        List<ClienteMotoristaTest> motoristas = new ArrayList<>();
        
        ClienteMotoristaTest m1 = new ClienteMotoristaTest("M001", "João Silva", "ABC1234", -23.5505, -46.6333);
        m1.conectarERegistrar();
        motoristas.add(m1);
        info("M001 - João Silva registrado em (-23.5505, -46.6333)");
        
        Thread.sleep(300);
        
        ClienteMotoristaTest m2 = new ClienteMotoristaTest("M002", "Maria Santos", "XYZ9876", -23.5600, -46.6500);
        m2.conectarERegistrar();
        motoristas.add(m2);
        info("M002 - Maria Santos registrada em (-23.5600, -46.6500)");
        
        Thread.sleep(300);
        
        ClienteMotoristaTest m3 = new ClienteMotoristaTest("M003", "Pedro Costa", "DEF4567", -23.5400, -46.6200);
        m3.conectarERegistrar();
        motoristas.add(m3);
        info("M003 - Pedro Costa registrado em (-23.5400, -46.6200)");
        
        sucesso(motoristas.size() + " motoristas registrados e disponíveis");
        
        return motoristas;
    }
    
    private static void testeCenario1_MatchingBasico() throws Exception {
        secao("CENÁRIO 1: Matching Básico");
        info("Solicitando corrida STANDARD próxima ao centro");
        
        ServicoDespachante despacho = (ServicoDespachante) registry.lookup("DespachanteCorridas");
        
        InfoPassageiro passageiro = new InfoPassageiro("P001", "Ana Oliveira");
        Localizacao origem = new Localizacao(-23.5505, -46.6333);
        Localizacao destino = new Localizacao(-23.5600, -46.6400);
        
        BilheteCorrida bilhete = despacho.solicitarCorrida(passageiro, origem, destino, Prioridade.STANDARD);
        info("Bilhete gerado: " + bilhete.getCorridaId());
        
        for (int i = 0; i < 6; i++) {
            Thread.sleep(500);
            BilheteCorrida status = despacho.consultarCorrida(bilhete.getCorridaId());
            System.out.print(".");
            
            if (status.getStatus() == StatusRequisicao.ATRIBUIDA) {
                System.out.println();
                sucesso("Corrida atribuída ao motorista: " + status.getMotoristaId());
                info("Esperado: M001 (João) - mais próximo do centro");
                return;
            }
        }
        
        System.out.println();
        BilheteCorrida statusFinal = despacho.consultarCorrida(bilhete.getCorridaId());
        if (statusFinal.getStatus() == StatusRequisicao.EXPIRADA) {
            alerta("Corrida expirou (timeout de matching)");
        }
    }
    
    private static void testeCenario2_PrioridadeVIP() throws Exception {
        secao("CENÁRIO 2: Prioridade VIP");
        info("Solicitando duas corridas: 1 STANDARD + 1 VIP");
        
        ServicoDespachante despacho = (ServicoDespachante) registry.lookup("DespachanteCorridas");
        
        InfoPassageiro p1 = new InfoPassageiro("P002", "Carlos Lima");
        BilheteCorrida b1 = despacho.solicitarCorrida(p1, new Localizacao(-23.5510, -46.6340), new Localizacao(-23.5700, -46.6500), Prioridade.STANDARD);
        info("STANDARD solicitada: " + b1.getCorridaId());
        
        Thread.sleep(100);
        
        InfoPassageiro p2 = new InfoPassageiro("P003", "Beatriz Souza");
        BilheteCorrida b2 = despacho.solicitarCorrida(p2, new Localizacao(-23.5405, -46.6205), new Localizacao(-23.5300, -46.6100), Prioridade.VIP);
        info("VIP solicitada: " + b2.getCorridaId());
        
        Thread.sleep(3000);
        
        BilheteCorrida status1 = despacho.consultarCorrida(b1.getCorridaId());
        BilheteCorrida status2 = despacho.consultarCorrida(b2.getCorridaId());
        
        if (status1.getStatus() == StatusRequisicao.ATRIBUIDA) {
            sucesso("STANDARD atribuída a: " + status1.getMotoristaId());
        }
        
        if (status2.getStatus() == StatusRequisicao.ATRIBUIDA) {
            sucesso("VIP atribuída a: " + status2.getMotoristaId());
            info("Esperado: M003 (Pedro) - mais próximo da origem VIP");
        }
    }
    
    private static void testeCenario3_Concorrencia() throws Exception {
        secao("CENÁRIO 3: Concorrência (10 requisições simultâneas)");
        info("Enviando múltiplas requisições ao mesmo tempo");
        
        ServicoDespachante despacho = (ServicoDespachante) registry.lookup("DespachanteCorridas");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<BilheteCorrida>> futures = new ArrayList<>();
        
        long inicio = System.currentTimeMillis();
        
        for (int i = 0; i < 10; i++) {
            final int num = i;
            Future<BilheteCorrida> future = executor.submit(() -> {
                try {
                    InfoPassageiro p = new InfoPassageiro("P" + String.format("%03d", num), "Passageiro" + num);
                    Localizacao origem = new Localizacao(-23.5500 + (Math.random() - 0.5) * 0.01, -46.6300 + (Math.random() - 0.5) * 0.01);
                    Localizacao destino = new Localizacao(-23.5600 + (Math.random() - 0.5) * 0.01, -46.6400 + (Math.random() - 0.5) * 0.01);
                    Prioridade prio = (num % 3 == 0) ? Prioridade.VIP : Prioridade.STANDARD;
                    
                    return despacho.solicitarCorrida(p, origem, destino, prio);
                } catch (Exception e) {
                    erro("Erro na requisição " + num + ": " + e.getMessage());
                    return null;
                }
            });
            futures.add(future);
        }
        
        List<BilheteCorrida> bilhetes = new ArrayList<>();
        for (Future<BilheteCorrida> future : futures) {
            BilheteCorrida b = future.get();
            if (b != null) bilhetes.add(b);
        }
        
        long tempoEnvio = System.currentTimeMillis() - inicio;
        info("10 requisições enviadas em " + tempoEnvio + "ms");
        
        Thread.sleep(4000);
        
        int atribuidas = 0;
        int expiradas = 0;
        int pendentes = 0;
        
        for (BilheteCorrida bilhete : bilhetes) {
            BilheteCorrida status = despacho.consultarCorrida(bilhete.getCorridaId());
            switch (status.getStatus()) {
                case ATRIBUIDA: atribuidas++; break;
                case EXPIRADA: expiradas++; break;
                case PENDENTE: pendentes++; break;
                case null: default: break;
            }
        }
        
        sucesso("Resultado: " + atribuidas + " atribuídas, " + expiradas + " expiradas, " + pendentes + " pendentes");
        info("Limitado a 3 motoristas disponíveis");
        
        executor.shutdown();
    }
    
    private static void testeCenario4_TimeoutConfirmacao(List<ClienteMotoristaTest> motoristas) throws Exception {
        secao("CENÁRIO 4: Timeout de Confirmação");
        info("Desabilitando auto-confirmação de um motorista");
        
        if (!motoristas.isEmpty()) {
            motoristas.get(0).setAutoConfirmar(false);
            info("Motorista " + motoristas.get(0).getMotoristaId() + " não confirmará corridas");
        }
        
        ServicoDespachante despacho = (ServicoDespachante) registry.lookup("DespachanteCorridas");
        
        InfoPassageiro p = new InfoPassageiro("P100", "Teste Timeout");
        BilheteCorrida bilhete = despacho.solicitarCorrida(p, new Localizacao(-23.5505, -46.6333), new Localizacao(-23.5600, -46.6400), Prioridade.STANDARD);
        
        info("Corrida " + bilhete.getCorridaId() + " solicitada");
        info("Aguardando timeout de confirmação (2s) e reatribuição...");
        
        Thread.sleep(5000);
        
        BilheteCorrida status = despacho.consultarCorrida(bilhete.getCorridaId());
        if (status.getStatus() == StatusRequisicao.ATRIBUIDA) {
            sucesso("Corrida reatribuída com sucesso para: " + status.getMotoristaId());
            info("Sistema detectou timeout e tentou outro motorista");
        } else {
            alerta("Status final: " + status.getStatus());
        }
        
        // Reabilitar confirmação
        if (!motoristas.isEmpty()) {
            motoristas.get(0).setAutoConfirmar(true);
        }
    }
    
    private static void testeCenario5_Cancelamento() throws Exception {
        secao("CENÁRIO 5: Cancelamento de Corrida");
        info("Solicitando corrida e cancelando antes da atribuição");
        
        ServicoDespachante despacho = (ServicoDespachante) registry.lookup("DespachanteCorridas");
        
        InfoPassageiro p = new InfoPassageiro("P200", "Teste Cancelamento");
        BilheteCorrida bilhete = despacho.solicitarCorrida(p, new Localizacao(-23.5505, -46.6333), new Localizacao(-23.5600, -46.6400), Prioridade.STANDARD);
        
        info("Corrida " + bilhete.getCorridaId() + " solicitada");
        
        Thread.sleep(100);
        boolean cancelado = despacho.cancelarCorrida(bilhete.getCorridaId());
        
        if (cancelado) {
            sucesso("Corrida cancelada com sucesso");
            BilheteCorrida status = despacho.consultarCorrida(bilhete.getCorridaId());
            if (status.getStatus() == StatusRequisicao.CANCELADA) {
                sucesso("Status confirmado: CANCELADA");
            }
        } else {
            alerta("Não foi possível cancelar (pode já estar atribuída)");
        }
    }
    
    private static void testeCenario6_TimeoutMatching() throws Exception {
        secao("CENÁRIO 6: Timeout de Matching");
        info("Marcando todos motoristas como ocupados e solicitando corrida");
        
        ServicoDespachante despacho = (ServicoDespachante) registry.lookup("DespachanteCorridas");
        
        try {
            despacho.atualizarStatus("M001", StatusMotorista.OFFLINE);
            despacho.atualizarStatus("M002", StatusMotorista.OFFLINE);
            despacho.atualizarStatus("M003", StatusMotorista.OFFLINE);
            info("Todos os motoristas marcados como OFFLINE");
        } catch (Exception e) {
            alerta("Erro ao marcar motoristas como OFFLINE: " + e.getMessage());
        }
        
        InfoPassageiro p = new InfoPassageiro("P300", "Teste Timeout");
        BilheteCorrida bilhete = despacho.solicitarCorrida(p, new Localizacao(-23.5505, -46.6333), new Localizacao(-23.5600, -46.6400), Prioridade.VIP);
        
        info("Corrida " + bilhete.getCorridaId() + " solicitada");
        info("Aguardando timeout de matching (3s)...");
        
        Thread.sleep(3500);
        
        BilheteCorrida status = despacho.consultarCorrida(bilhete.getCorridaId());
        if (status.getStatus() == StatusRequisicao.EXPIRADA) {
            sucesso("Corrida expirou corretamente após 3 segundos");
        } else {
            alerta("Status: " + status.getStatus() + " (esperado: EXPIRADA)");
        }
        
        // Reativar motoristas
        try {
            despacho.atualizarStatus("M001", StatusMotorista.DISPONIVEL);
            despacho.atualizarStatus("M002", StatusMotorista.DISPONIVEL);
            despacho.atualizarStatus("M003", StatusMotorista.DISPONIVEL);
            info("Motoristas reativados");
        } catch (Exception e) {
            alerta("Erro ao reativar motoristas: " + e.getMessage());
        }
    }
    
    private static void testeCenario7_Stress() throws Exception {
        secao("CENÁRIO 7: Teste de Stress (50 requisições em 10s)");
        info("Enviando 50 requisições de passageiros com 3 motoristas");
        
        ServicoDespachante despacho = (ServicoDespachante) registry.lookup("DespachanteCorridas");
        ExecutorService executor = Executors.newFixedThreadPool(20);
        
        List<Future<BilheteCorrida>> futures = new ArrayList<>();
        long inicio = System.currentTimeMillis();
        
        for (int i = 0; i < 50; i++) {
            final int num = i;
            
            if (i > 0 && i % 10 == 0) {
                Thread.sleep(200);
            }
            
            Future<BilheteCorrida> future = executor.submit(() -> {
                try {
                    InfoPassageiro p = new InfoPassageiro("STRESS" + num, "Passageiro" + num);
                    Localizacao origem = new Localizacao(-23.5500 + (Math.random() - 0.5) * 0.02, -46.6300 + (Math.random() - 0.5) * 0.02);
                    Localizacao destino = new Localizacao(-23.5600 + (Math.random() - 0.5) * 0.02, -46.6400 + (Math.random() - 0.5) * 0.02);
                    Prioridade prio = (num % 4 == 0) ? Prioridade.VIP : Prioridade.STANDARD;
                    
                    return despacho.solicitarCorrida(p, origem, destino, prio);
                } catch (Exception e) {
                    return null;
                }
            });
            futures.add(future);
        }
        
        List<BilheteCorrida> bilhetes = new ArrayList<>();
        for (Future<BilheteCorrida> future : futures) {
            try {
                BilheteCorrida b = future.get(1, TimeUnit.SECONDS);
                if (b != null) bilhetes.add(b);
            } catch (Exception e) {
                erro("Erro na requisição: " + e.getMessage());
            }
        }
        
        long tempoEnvio = System.currentTimeMillis() - inicio;
        info("50 requisições enviadas em " + tempoEnvio + "ms");
        
        info("Aguardando processamento de todas as corridas...");
        Thread.sleep(8000);
        
        int atribuidas = 0;
        int expiradas = 0;
        int canceladas = 0;
        int pendentes = 0;
        int falhas = 0;
        
        for (BilheteCorrida bilhete : bilhetes) {
            try {
                BilheteCorrida status = despacho.consultarCorrida(bilhete.getCorridaId());
                switch (status.getStatus()) {
                    case ATRIBUIDA: atribuidas++; break;
                    case EXPIRADA: expiradas++; break;
                    case CANCELADA: canceladas++; break;
                    case PENDENTE: pendentes++; break;
                    case FALHOU: falhas++; break;
                }
            } catch (Exception e) {
                erro("Erro ao consultar corrida: " + e.getMessage());
            }
        }
        
        long tempoTotal = System.currentTimeMillis() - inicio;
        double throughput = (atribuidas * 1000.0) / tempoTotal;
        
        System.out.println();
        sucesso("═══ ESTATÍSTICAS DO TESTE DE STRESS ═══");
        info("Total de requisições: " + bilhetes.size());
        info("Atribuídas: " + atribuidas);
        info("Expiradas: " + expiradas);
        info("Pendentes: " + pendentes);
        info("Falhas: " + falhas);
        info("Tempo total: " + tempoTotal + "ms");
        info("Throughput: " + String.format("%.2f", throughput) + " corridas/segundo");
        
        if (throughput >= 5.0) {
            sucesso("Throughput atende RNF3 (>= 5 corridas/s)");
        } else {
            alerta("Throughput abaixo do esperado (< 5 corridas/s)");
        }
        
        executor.shutdown();
    }
    
    private static void finalizarTeste() {
        info("\nFinalizando teste...");
        
        try {
            if (servidor != null) {
                servidor.shutdown();
            }
            
            if (registry != null) {
                registry.unbind("DespachanteCorridas");
            }
            
            sucesso("Recursos liberados com sucesso");
        } catch (Exception e) {
            erro("Erro ao finalizar: " + e.getMessage());
        }
        
        System.out.println("\n" + CYAN + "Teste finalizado. Até logo!" + RESET + "\n");
    }
    
    private static void secao(String titulo) {
        System.out.println("\n" + PURPLE + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println(PURPLE + " " + titulo + RESET);
        System.out.println(PURPLE + "═══════════════════════════════════════════════════════════" + RESET);
    }
    
    private static void info(String msg) {
        System.out.println(BLUE + "[INFO] " + RESET + msg);
    }
    
    private static void sucesso(String msg) {
        System.out.println(GREEN + "[SUCESSO] " + RESET + msg);
    }
    
    private static void alerta(String msg) {
        System.out.println(YELLOW + "[ALERTA] " + RESET + msg);
    }
    
    private static void erro(String msg) {
        System.out.println(RED + "[ERRO] " + RESET + msg);
    }
}

class ClienteMotoristaTest extends UnicastRemoteObject implements CallbackMotorista {
    
    private String motoristaId;
    private String nome;
    private String placa;
    private Localizacao posicao;
    private ServicoDespachante despachante;
    private boolean autoConfirmar = true;
    
    public ClienteMotoristaTest(String id, String nome, String placa, double lat, double lon) throws RemoteException {
        super();
        this.motoristaId = id;
        this.nome = nome;
        this.placa = placa;
        this.posicao = new Localizacao(lat, lon);
    }
    
    public void conectarERegistrar() throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        despachante = (ServicoDespachante) registry.lookup("DespachanteCorridas");
        
        InfoMotorista info = new InfoMotorista(motoristaId, nome, placa, posicao);
        despachante.registrarMotorista(info, this);
    }
    
    @Override
    public void aoAtribuir(AtribuicaoCorrida atribuicao) throws RemoteException {
        if (autoConfirmar) {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    despachante.aceitarAtribuicao(atribuicao.getAtribuicaoId());
                    
                    Thread.sleep(1000);
                    despachante.iniciarCorrida(atribuicao.getAtribuicaoId());
                    
                    Thread.sleep(2000);
                    despachante.concluirCorrida(atribuicao.getAtribuicaoId());
                } catch (Exception e) {
                    System.err.println("Erro ao processar corrida: " + e.getMessage());
                }
            }).start();
        }
    }
    
    @Override
    public void aoCancelar(String atribuicaoId) throws RemoteException {
        // Apenas log
    }
    
    public void setAutoConfirmar(boolean auto) {
        this.autoConfirmar = auto;
    }
    
    public String getMotoristaId() {
        return motoristaId;
    }
}