package Threads;

// Classe principal que demonstra o uso de sincronização em threads
public class MySynchronized {
    // Variável compartilhada entre as threads
    public static int i = 0;

    public static void main(String[] args){
        // Cria uma instância de myRunnable
        myRunnable r = new myRunnable();

        // Cria quatro threads que compartilham o mesmo Runnable
        Thread t0 = new Thread(r);
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        Thread t3 = new Thread(r);
        
        // Inicia as threads
        t0.start();
        t1.start();
        t2.start();
        t3.start();
    }

    // Método sincronizado para garantir acesso exclusivo à variável i
    //é como uma sala com a chave na porta
    //só uma thread pode executar esse bloco de cada vez
    //a outra q chegar tem q esperar a primeira sair para liberar a chave
    public static void imprime(){
        // Apenas uma thread pode executar este bloco por vez
        synchronized (MySynchronized.class){
            i++; // Incrementa a variável compartilhada
            String name = Thread.currentThread().getName(); // Obtém o nome da thread atual
            System.out.println(name + " : " + i); // Exibe o nome da thread e o valor de i
        }
    }

    // Classe interna que implementa Runnable
    public static class myRunnable implements Runnable {
        @Override
        public void run(){
            imprime(); // Chama o método sincronizado
        }
    }
}
