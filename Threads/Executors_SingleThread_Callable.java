package Threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executors_SingleThread_Callable {
    public static void main(String[] args) {
        
        // --- TÓPICO 10: EXECUTORES (simples) ---
        // Em vez de 'new Thread(r).start()', que é caro (cria
        // e destrói uma thread do zero), nós criamos um "Pool".
        //
        // 'Executors.newSingleThreadExecutor()' cria um pool
        // com UMA ÚNICA thread trabalhadora.
        // Isso é útil para garantir que as tarefas sejam executadas
        // EM SEQUÊNCIA (uma após a outra), mas em uma thread
        // *diferente* da 'main'.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        // 2. Submetemos a "tarefa" (Runnable) ao pool.
        // A thread do pool vai pegar esta tarefa da fila e executá-la.
        executor.execute(new myRunnable());
        // Se chamássemos execute() de novo, a nova tarefa
        // esperaria a primeira terminar.

        // 3. Desliga o pool.
        // 'shutdown()' é uma parada graciosa: o pool não aceita
        // novas tarefas, mas termina as que já estão na fila.
        executor.shutdown();
    }

    // Esta é a "tarefa" (Runnable)
    public static class myRunnable implements Runnable{
        public void run(){
            // O nome da thread será algo como "pool-1-thread-1"
            String nome = Thread.currentThread().getName();
            System.out.println(nome + ": LP3");
        }
    }
}