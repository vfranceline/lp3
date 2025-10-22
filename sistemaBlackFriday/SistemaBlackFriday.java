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
        PriorityBlockingQueue<Pedido> fila = new PriorityBlockingQueue<>(50);
        GerenciadorEstoque manager = new GerenciadorEstoque();
        AtomicInteger processados = new AtomicInteger(0);
        AtomicInteger rejeitados = new AtomicInteger(0);
        AtomicInteger qntPedidoGerado = new AtomicInteger(0);

        ExecutorService produtores = Executors.newFixedThreadPool(3);
        ExecutorService consumidores = Executors.newFixedThreadPool(5);
        ExecutorService monitorExe = Executors.newSingleThreadExecutor();

        System.out.println("[Monitor] Iniciando monitoramento a cada 2 segundos");
        Monitor monitor = new Monitor(manager, fila, processados, rejeitados, qntPedidoGerado);
        monitorExe.execute(monitor);

        for(int i = 0; i < 3; i++){
            String[] tipos = {"API", "Web", "Mobile"};
            String nome = tipos[i];
            
            Produtor produtor = new Produtor(nome, 20, fila, qntPedidoGerado);
            produtores.execute(produtor);
        }

        for(int i = 1; i <= 55; i++){
            String nomeConsumidor = "Consumidor-" + i;
            Consumidor consumidor = new Consumidor(nomeConsumidor, manager, fila, processados, rejeitados);
            consumidores.execute(consumidor);
        }
        produtores.shutdown();
        consumidores.shutdown();

        try {
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
