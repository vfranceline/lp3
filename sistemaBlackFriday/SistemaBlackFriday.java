package sistemaBlackFriday;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SistemaBlackFriday {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // --- TÓPICO 7: COLEÇÕES PARA CONCORRÊNCIA (FILAS) ---
        // 1. Cria a fila (o "buffer" compartilhado).
        // É uma Fila de Bloqueio (BlockingQueue)
        // com Prioridade (Priority), baseada no Pedido.compareTo().
        PriorityBlockingQueue<Pedido> fila = new PriorityBlockingQueue<>(50);        
        
        // 2. Cria o Recurso Crítico (com Locks)
        GerenciadorEstoque manager = new GerenciadorEstoque();

        // --- TÓPICO 8: CLASSES ATÔMICAS ---
        // 3. Cria os contadores compartilhados
        AtomicInteger processados = new AtomicInteger(0);
        AtomicInteger rejeitados = new AtomicInteger(0);
        AtomicInteger qntPedidoGerado = new AtomicInteger(0);

// --- TÓPICOS 10 e 11: EXECUTORES ---
        // 4. Cria os "Pools de Threads" (Exércitos de Trabalhadores)
        
        // Tópico 11 (Múltiplas): 3 threads para os Produtores
        ExecutorService produtores = Executors.newFixedThreadPool(3);
        // Tópico 11 (Múltiplas): 5 threads para os Consumidores
        ExecutorService consumidores = Executors.newFixedThreadPool(5);
        // Tópico 10 (Simples): 1 thread para o Monitor
        ExecutorService monitorExe = Executors.newSingleThreadExecutor();

        // 5. Inicia o Monitor
        System.out.println("[Monitor] Iniciando monitoramento a cada 2 segundos");
        Monitor monitor = new Monitor(manager, fila, processados, rejeitados, qntPedidoGerado);
        monitorExe.execute(monitor);

        // 6. Submete as tarefas Produtor (Tópico 4) ao pool (Tópico 11)
        for(int i = 0; i < 3; i++){
            String[] tipos = {"API", "Web", "Mobile"};
            String nome = tipos[i];
            
            Produtor produtor = new Produtor(nome, 20, fila, qntPedidoGerado);
            produtores.execute(produtor);
        }

        // 7. Submete as tarefas Consumidor (Tópico 4) ao pool (Tópico 11)
        for(int i = 1; i <= 5; i++){
            String nomeConsumidor = "Consumidor-" + i;
            Consumidor consumidor = new Consumidor(nomeConsumidor, manager, fila, processados, rejeitados);
            consumidores.execute(consumidor);
        }

        // 8. Gerencia o desligamento (Tópico 2)
        produtores.shutdown(); // Não aceita mais produtores
        consumidores.shutdown(); // Não aceita mais consumidores

        try {
            // A thread 'main' espera (TIMED_WAITING)
            // os produtores terminarem.
            
            // 1. Espera os produtores terminarem
            if (!produtores.awaitTermination(1, TimeUnit.MINUTES)) {
                System.err.println("Produtores não terminaram, forçando desligamento.");
                produtores.shutdownNow();
            }
            System.out.println(">>> Todos os produtores finalizaram! <<<");

            // 2. Espera os consumidores terminarem
            if (!consumidores.awaitTermination(5, TimeUnit.MINUTES)) {
                System.err.println("Consumidores não terminaram, forçando desligamento.");
                consumidores.shutdownNow();
            }
            System.out.println(">>> Todos os consumidores finalizaram! <<<");

            // 3. Agora que produtores E consumidores terminaram, paramos o monitor
            monitor.setRunning(false); // Diz para a thread parar o loop
            monitorExe.shutdown(); // Desliga o executor do monitor
            if (!monitorExe.awaitTermination(30, TimeUnit.SECONDS)) {
                monitorExe.shutdownNow();
            }
            System.out.println("[Monitor] Encerrado.");

            long endTime = System.currentTimeMillis();
            long tempoTotal = (endTime - startTime) / 1000; // Tempo em segundos

            System.out.println("==============================================");
            System.out.println("           RELATÓRIO FINAL DE EXECUÇÃO        ");
            System.out.println("==============================================");
            System.out.println("Total de pedidos gerados: " + qntPedidoGerado.get());
            System.out.println("Processados com sucesso: " + processados.get()); 
            System.out.println("Rejeitados (sem estoque): " + rejeitados.get()); 
            System.out.println("Tempo total de execução: " + tempoTotal + "s"); 
            System.out.println("==============================================");
            System.out.println("                 ESTOQUE FINAL                ");
            System.out.println("==============================================");

            // Pega o estoque final usando o novo método
            Map<String, Integer> estoqueFinal = manager.getEstoqueCompleto();
            
            // Imprime o estoque ordenado por nome
            estoqueFinal.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    System.out.printf("%-10s : %d unidades\n", 
                                    entry.getKey(), entry.getValue());
                });
            
            System.out.println("==============================================");
            System.out.println("--- SISTEMA FINALIZADO COM SUCESSO ---");


        } catch (InterruptedException e) {
            // Se a thread 'main' for interrompida, força o desligamento de tudo
            System.err.println("Aplicação interrompida! Forçando desligamento...");
            produtores.shutdownNow();
            consumidores.shutdownNow();
            monitorExe.shutdownNow();
        }
    }
}
