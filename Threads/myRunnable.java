package Threads;

public class myRunnable implements Runnable{
    // --- TÓPICO 5: O PROBLEMA (CONDIÇÃO DE CORRIDA) ---
    // 'count' é uma variável 'static'. Isso significa que TODAS
    // as threads, mesmo que tenham instâncias *diferentes* de 'myRunnable',
    // compartilharão esta ÚNICA variável 'count'.
    static int count = 0;
    
    // --- TÓPICO 4: CRIAÇÃO COM RUNNABLE ---
    // Ao implementar 'Runnable', esta classe se torna uma "tarefa"
    // que pode ser executada por uma thread. O código
    // que a thread executará está aqui no método 'run()'.
    @Override
    public void run(){
        String name = Thread.currentThread().getName();
        
        // --- O PROBLEMA (CONDIÇÃO DE CORRIDA) ---
        // A operação 'count++' não é "atômica". Ela é, na verdade, 3 passos:
        // 1. LER o valor de 'count' (ex: 0)
        // 2. SOMAR 1 ao valor lido (ex: 1)
        // 3. ESCREVER o novo valor em 'count' (ex: 1)
        //
        // Se a Thread-A ler (0) e a Thread-B ler (0) ao mesmo tempo,
        // ambas vão escrever (1). O resultado final será 1,
        // quando o correto deveria ser 2.
        // Isto é uma "Condição de Corrida" (Race Condition).
        System.out.println(name + " --> " + count++);
    }
}