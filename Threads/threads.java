package Threads;

public class threads {
    public static void main(String[] args) throws InterruptedException{
        // forma 1: implementando a interface Runnable
        //a classe myRunnable tem a tarefa q a thread vai executar
        myRunnable runnable = new myRunnable();

        //criando uma nova thread passando o objeto runnable como tarefa
        Thread t = new Thread(runnable);
        //inicia a execução da thread
        t.start();
        

        //----------------------------------------------------------------------------

        // forma 2: usando uma expressão lambda (+ moderno e conciso)
        //tarefa é definida diretamente aqui
        Runnable lambdaRunnable = () -> {
            // Loop para simular etapas da tarefa
            for (int i = 1; i <= 5; i++) {
            // Exibe o nome da thread e o passo atual
            System.out.println(Thread.currentThread().getName() + " - step " + i);
            try {
                // Pausa a thread por 500 milissegundos
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Trata possíveis interrupções durante o sleep
                e.printStackTrace();
            }
            }
        };

        //cria a thread com a tarefa q foi definida na lambda
        Thread t1 = new Thread(lambdaRunnable);
        t1.start(); //iniciando a thread

        // o start() não executa o código imediatamente, ele agenda a tarefa para ser executada em 
        // paralelo pela JVM.
    }
}
