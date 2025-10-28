package lista.ex8;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimuladorTarefas {
    public static void main(String[] args) {
        
        ExecutorService pool = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++){
            ContadorPalavras tarefa = new ContadorPalavras("tarefa #"+ i);
            pool.execute(tarefa);
        }

        pool.shutdown();

        try {
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("tempo limite atingido, encerrado");
                pool.shutdownNow();
                
            }
        } catch (Exception e) {
            pool.shutdownNow();
        }
    }
}
