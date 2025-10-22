package BEstacionamentos;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParkingSimulator {
    public static void main(String[] args) {
        Random random = new Random();

        // 1. Cria UMA instância do ParkingManager.
        // Este único objeto será compartilhado por TODAS as threads (veículos).
        ParkingManager PManager = new ParkingManager();

        // --- TÓPICO 11: EXECUTORES (MÚLTIPLAS EXECUÇÕES) ---
        // Em vez de 'new Thread(new Vehicle(...)).start()' 20 vezes,
        // criamos um "Pool de Threads" (piscina de trabalhadores).
        //
        // 'Executors.newFixedThreadPool(4)' cria um pool com
        // exatamente 4 threads "trabalhadoras". Essas 4 threads
        // ficarão vivas e pegarão as 20 tarefas (Veículos) da fila
        // para executar.
        // Isso é muito mais eficiente, pois evita o custo de
        // criar e destruir 20 threads.
        ExecutorService executor = Executors.newFixedThreadPool(4);


        System.out.println("=== Sistema de Estacionamento Inteligente === \n === GERANDO VEICULOS ===");

        // 2. Cria as 20 tarefas (Veículos)
        for(int i = 1; i <= 20; i++){
            //sorteia um numero entre 0 a 99, se for menor doq 30 é prioritario
            String tipo = random.nextInt(100) < 30 ? "PRIORITARIO" : "NORMAL";

            // Cria a "tarefa" (um Runnable), passando o ID e o manager compartilhado.
            Vehicle vehicle = new Vehicle(tipo, i, PManager);
            System.out.println("Gerado: Veiculo #" + i + "(" + tipo + ")");
            
            // 3. Submete a tarefa ao pool.
            // 'execute()' coloca o 'vehicle' (Runnable) em uma fila interna.
            // Assim que uma das 4 threads do pool estiver livre,
            // ela pegará esta tarefa e executará seu método 'run()'.
            executor.execute(vehicle);
        }

        // 4. Desliga o Executor
        // 'shutdown()' é uma parada "graciosa". O executor
        // NÃO aceita mais novas tarefas, mas ele VAI TERMINAR
        // todas as 20 tarefas que já estão na fila.
        executor.shutdown();

        try {
            System.out.println("\n === INICIANDO SIMULAÇÃO ===\n");

            // 5. Aguarda o término da simulação
            // A thread 'main' (que está rodando este 'main()')
            // vai parar aqui e esperar.
            // 'awaitTermination(30, TimeUnit.SECONDS)' faz a thread 'main'
            // entrar em "TIMED_WAITING" (Tópico 2).
            // Ela acorda se:
            // a) Todas as 20 tarefas terminarem (retorna true).
            // b) O tempo de 30 segundos estourar (retorna false).
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("tempo limite atingido! forçando o encerramento aq, vai q né");
                // Se o tempo estourou, 'shutdownNow()' tenta interromper
                // as threads que ainda estiverem rodando.
                executor.shutdownNow();
            }
        } catch (Exception e) {
            executor.shutdownNow();
        }

        // 6. Imprime o Relatório Final
        // Este código só é executado DEPOIS que a simulação
        // (todas as threads) terminou.
        System.out.println("\n === SIMULAÇÃO CONCLUIDA ===");
        PManager.printStats();
        
    }
}
