package Threads;

public class Volatile {

    // --- TÓPICO 9: VOLATILE (Método Volatil / Variável Volátil) ---
    // O problema:
    // Sem 'volatile', a Thread 't0' (rodando no Core 1)
    // pode "cachear" o valor 'false' de 'preparado'.
    // A thread 'main' (rodando no Core 2) atualiza 'preparado'
    // para 'true' na memória principal (RAM), mas 't0'
    // continua olhando para o seu cache 'false' e entra em loop infinito.
    //
    // A SOLUÇÃO (que está faltando no código):
    // Deveria ser: 'private static volatile boolean preparado = false;'
    //
    // 'volatile' força com que todas as LEITURAS e ESCRITAS
    // de 'preparado' sejam feitas diretamente na MEMÓRIA PRINCIPAL,
    // garantindo que a mudança feita pela 'main' seja *visível* para 't0'.
    private static int numero = 0;
    private static boolean preparado = false;

    private static class myRunnable implements Runnable{
        @Override
        public void run(){
            // Esta é uma "espera ocupada" (busy-waiting).
            // A thread 't0' fica rodando em círculos, consumindo CPU.
            while (!preparado){
                // Thread.yield() é uma "dica" para a JVM
                // de que esta thread está disposta a ceder seu
                // tempo de CPU.
                Thread.yield();
            }
            // Só sai do loop quando 'preparado' se torna true.
            // (O que só é garantido se 'preparado' for 'volatile')
            System.out.println("num: " + numero);
        }
    }

    public static void main(String[] args) {
        // A "thread leitora" (t0) é iniciada
        Thread t0 = new Thread(new myRunnable());
        t0.start();
        
        // A "thread escritora" (main) atualiza os valores
        numero = 42;
        preparado = true; // A t0 precisa VER esta mudança.
    }
}