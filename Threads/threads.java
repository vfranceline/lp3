package Threads;

public class threads {
    public static void main(String[] args) throws InterruptedException{
        
        // --- TÓPICO 4: CRIAÇÃO COM RUNNABLE (Forma Clássica) ---
        
        // 1. A "tarefa" é definida na classe 'myRunnable'.
        myRunnable runnable = new myRunnable();

        // 2. O "trabalhador" (Thread) é criado, recebendo a tarefa.
        // Estado: NEW (Tópico 2)
        Thread t = new Thread(runnable);
        
        // 3. Inicia o trabalhador.
        // O .start() agenda a execução do método 'run()' da tarefa
        // em uma nova thread.
        // Estado: NEW -> RUNNABLE (Tópico 2)
        t.start();
        

        //----------------------------------------------------------------------------

        // --- TÓPICO 3: CRIAÇÃO COM LAMBDA (Forma Moderna) ---
        
        // Em vez de criar um arquivo .java (como 'myRunnable.java')
        // só para definir a tarefa, podemos defini-la "inline"
        // usando uma expressão Lambda.
        // O '() -> { ... }' é uma implementação anônima da
        // interface 'Runnable'.
        Runnable lambdaRunnable = () -> {
            // Este é o corpo do método 'run()'
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName() + " - step " + i);
                try {
                    // --- TÓPICO 2: CICLO DE VIDA DE THREAD ---
                    // Thread.sleep() pausa a thread ATUAL.
                    // Ela entra no estado TIMED_WAITING e não
                    // consome CPU, permitindo que outras threads rodem.
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // O resto do processo é idêntico:
        Thread t1 = new Thread(lambdaRunnable); // Estado: NEW
        t1.start(); // Estado: RUNNABLE
    }
}