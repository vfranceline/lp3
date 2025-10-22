package Threads;

// Classe principal que demonstra o uso de sincronização em threads
public class MySynchronized {
    
    // --- TÓPICO 5: synchronized (variável) ---
    // Esta é a variável compartilhada (o "recurso crítico")
    // que precisa ser protegida.
    public static int i = 0;

    public static void main(String[] args){
        // Cria UMA ÚNICA instância da tarefa
        myRunnable r = new myRunnable();

        // Cria quatro threads que compartilham a MESMA TAREFA.
        // Todas as 4 threads tentarão executar o 'imprime()' ao mesmo tempo.
        Thread t0 = new Thread(r);
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        Thread t3 = new Thread(r);
        
        // Inicia todas
        t0.start();
        t1.start();
        t2.start();
        t3.start();
        // Sem o 'synchronized', a saída seria uma bagunça (ex: 1, 2, 2, 3)
        // Com 'synchronized', a saída será ordenada (1, 2, 3, 4)
    }

    // --- TÓPICO 5: synchronized (função / classe) ---
    public static void imprime(){
        
        // O bloco 'synchronized' é a "sala" com a chave.
        // Apenas UMA thread pode "entrar" neste bloco por vez.
        //
        // A "chave" (o lock) é (MySynchronized.class).
        // Por que a CLASSE? Porque a variável 'i' e o método 'imprime'
        // são 'static'. Recursos estáticos pertencem à CLASSE,
        // não a um objeto.
        //
        // (Se 'i' e 'imprime' NÃO fossem static, usaríamos 'synchronized(this)')
        synchronized (MySynchronized.class){
            // --- INÍCIO DA SEÇÃO CRÍTICA ---
            // Quando a Thread-t0 entra aqui, as threads t1, t2, e t3
            // ficam no estado BLOCKED (Tópico 2), esperando a
            // chave ser liberada.
            
            i++; // Esta operação agora é segura.
            String name = Thread.currentThread().getName(); 
            System.out.println(name + " : " + i);
            
            // --- FIM DA SEÇÃO CRÍTICA ---
        } // A chave (lock) é liberada aqui. A próxima thread (ex: t1) entra.
    }

    // Classe interna que implementa Runnable
    public static class myRunnable implements Runnable {
        @Override
        public void run(){
            imprime(); // Chama o método sincronizado
        }
    }
}